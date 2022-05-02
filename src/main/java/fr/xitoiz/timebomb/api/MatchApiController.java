package fr.xitoiz.timebomb.api;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import fr.xitoiz.timebomb.dao.IDAOCard;
import fr.xitoiz.timebomb.dao.IDAOMatch;
import fr.xitoiz.timebomb.dao.IDAOUser;
import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.MatchState;
import fr.xitoiz.timebomb.enums.PlayerRole;
import fr.xitoiz.timebomb.exeption.CardNotFoundException;
import fr.xitoiz.timebomb.exeption.MatchNotFoundException;
import fr.xitoiz.timebomb.exeption.MatchNotJoinableException;
import fr.xitoiz.timebomb.exeption.MatchNotLeavableException;
import fr.xitoiz.timebomb.exeption.MatchNotPlayingException;
import fr.xitoiz.timebomb.exeption.MatchNotStartableException;
import fr.xitoiz.timebomb.exeption.PlayerInAMatchException;
import fr.xitoiz.timebomb.exeption.PlayerNotInAMatchException;
import fr.xitoiz.timebomb.exeption.PlayerNotInThisMatchException;
import fr.xitoiz.timebomb.exeption.PlayerNotYourTurnException;
import fr.xitoiz.timebomb.exeption.TransactionErrorException;
import fr.xitoiz.timebomb.models.Card;
import fr.xitoiz.timebomb.models.Match;
import fr.xitoiz.timebomb.models.User;
import fr.xitoiz.timebomb.projection.Views;
import fr.xitoiz.timebomb.services.MatchService;
import fr.xitoiz.timebomb.services.UserSession;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/match")
public class MatchApiController {
		
	@Autowired
	private IDAOMatch daoMatch;
	
	@Autowired
	private IDAOUser daoUser;
	
	@Autowired
	private IDAOCard daoCard;
	
	@Autowired
	private final UserSession userSession;
	
	private final MatchService matchService;
	
	public MatchApiController() {
		this.userSession = new UserSession();
		this.matchService = new MatchService();
	}
	
	@GetMapping("/mine")
	@JsonView(Views.Match.class)
	private Match getMatch() {
		User user = this.daoUser.getById(this.userSession.getId());
		return this.daoMatch.findById(user.getCurrentMatch().getId()).orElseThrow(PlayerNotInAMatchException::new);
	}
	
	@GetMapping("/admin")
	@JsonView(Views.MatchAdmin.class)
	private List<Match> getMatchesAdmin() {
		return this.daoMatch.findAll();
	}
	
	
	@PostMapping("/create")
	@JsonView(Views.Match.class)
	private Match createMatch(@Valid @RequestBody Match match) {
		User user = this.daoUser.getById(this.userSession.getId());
		if (user.getCurrentMatch() != null) {throw new PlayerInAMatchException();}
		
		match.setOwner(user);
		user.setCurrentMatch(match);
		
		this.daoMatch.save(match);
		System.out.println("Le match d'id " + match.getId() + " a été créé à la demande de " + user.getId() + " - " + user.getPseudo() + ".");
		
		return match;
	}
	
	@PostMapping("/join")
	private void joinMatch(@RequestBody Match matchRequested) {
		User user = this.daoUser.getById(this.userSession.getId());
		if (user.getCurrentMatch() != null) {throw new PlayerInAMatchException();}
		
		Match match = this.daoMatch.findById(matchRequested.getId()).orElseThrow(MatchNotFoundException::new);
		if (match.getState() != (MatchState.PENDING)) {throw new MatchNotJoinableException();}
		
		user.setCurrentMatch(match);
		this.daoUser.save(user);
		
		System.out.println("Le user " + user.getId() + "-" + user.getPseudo() + " a rejoint le match " + match.getId());
	}
	
	@PostMapping("/leave")
	private void leaveMatch(@RequestBody Match matchRequested) {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = user.getCurrentMatch();
		
		if (user.getCurrentMatch() == null) {throw new PlayerNotInAMatchException();}
		if (user.getCurrentMatch().getState() == MatchState.PLAYING) {throw new MatchNotLeavableException();}
		if (user.getCurrentMatch().getId() != matchRequested.getId()) {throw new PlayerNotInThisMatchException();}
		
		user.setCurrentMatch(null);
		this.daoUser.save(user);
		
		System.out.println("Le user " + user.getId() + "-" + user.getPseudo() + " a quitté le match " + match.getId());	
		
		if (match.getPlayerList().size() == 0) {
			switch (match.getState()) {
				case TERMINATED:
					this.daoCard.clearMatch(match.getId());
					System.out.println("Les cartes du match " + match.getId() + " ont été clear.");
					this.daoCard.deleteCardMatch(match.getId());
					System.out.println("Les cartes du match " + match.getId() + " ont été supprimées.");
				case PENDING:
					this.daoMatch.deleteById(match.getId());
					System.out.println("Le match " + match.getId() + " a été effacé.");
					break;
			default:
				System.out.println("Erreur switch");
				throw new TransactionErrorException();
			}
		}
	}
	
	@GetMapping("/start")
	@Transactional
	private void startMatch() {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = user.getCurrentMatch();
		
		if (match.getState() != MatchState.PENDING) {throw new MatchNotStartableException();}
		if (match.getPlayerList().size() > 8 || match.getPlayerList().size() < 4) {throw new MatchNotStartableException();}
		
		match = this.matchService.generateRole(match);
		match = this.matchService.generateCard(match);
		match = this.matchService.distributeCards(match);
				
		int randomNum = ThreadLocalRandom.current().nextInt(0, match.getPlayerList().size());
		match.setCurrentPlayer(match.getPlayerList().get(randomNum));
		match.setLastPlayer(match.getPlayerList().get(randomNum));
		match.setState(MatchState.PLAYING);
		
		this.daoCard.saveAll(match.getCardList());
		this.daoMatch.save(match);
				
		System.out.println("Le match " + match.getId() + " a été généré.");
		System.out.println("Les cartes du match "  + match.getId() + " de "+ match.getCardList().get(0).getId() + " à " + match.getCardList().get(match.getCardList().size() - 1).getId() + " a été généré.");
	}

	@PostMapping("/play")
	private void playCard(@RequestBody Card cardRequest) throws Exception {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = this.daoMatch.findById(user.getCurrentMatch().getId()).orElseThrow(MatchNotFoundException::new);
		Card card = this.daoCard.findById(cardRequest.getId()).orElseThrow(CardNotFoundException::new);
		
		if (match.getState() != MatchState.PLAYING) {throw new MatchNotPlayingException();}
		
		if (user.getId() != match.getCurrentPlayer().getId()) {throw new PlayerNotYourTurnException();}
		
		if (card.getOwner().getId() == match.getLastPlayer().getId()) {throw new Exception();}
		if (card.getOwner().getId() == match.getCurrentPlayer().getId()) {throw new Exception();}
		if (card.getState() != CardState.HIDDEN) {throw new Exception();}
		
		card.setState(CardState.REVEALED);
		System.out.println("La carte révélée par le joueur " + user.getPseudo() + " était une carte " + card.getType() );
		match.setLastPlayer(match.getCurrentPlayer());
		match.setCurrentPlayer(card.getOwner());
		
		switch (card.getType()) {
			case DIFFUSE:
				if (!matchService.isDefuseLeft(match)) {
					match.setWinnerRole(PlayerRole.SHERLOCK);
					match.setState(MatchState.TERMINATED);
					match = matchService.revealAllCards(match);
					break;
				}
			case BAIT:
				if (matchService.isMatchOver(match)) {
					match.setWinnerRole(PlayerRole.MORIARTY);
					match.setState(MatchState.TERMINATED);
					match = matchService.revealAllCards(match);
					System.out.println("Le match est terminé");
					break;
				}
				if(matchService.isRoundOver(match)) {
					match = matchService.distributeCards(match);
				}
				break;
			case BOMB:
				match.setWinnerRole(PlayerRole.MORIARTY);
				match.setState(MatchState.TERMINATED);
				match = matchService.revealAllCards(match);
				break;
		}
		
		this.daoMatch.save(match);
	}

}
