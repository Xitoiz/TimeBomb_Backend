package fr.xitoiz.timebomb.api;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fr.xitoiz.timebomb.exeption.CardAlreadyRevealedException;
import fr.xitoiz.timebomb.exeption.CardNotFoundException;
import fr.xitoiz.timebomb.exeption.CurrentPlayerCardException;
import fr.xitoiz.timebomb.exeption.LastPlayerCardException;
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
import fr.xitoiz.timebomb.exeption.UserNotFoundException;
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
	
	private final Logger logger;
	
	public MatchApiController() {
		this.userSession = new UserSession();
		this.logger = LoggerFactory.getLogger(MatchApiController.class);
		this.matchService = new MatchService();
	}
	
	@GetMapping("/mine")
	@JsonView(Views.Match.class)
	private Match getMatch() {
		User user = this.daoUser.findById(this.userSession.getId()).orElseThrow(UserNotFoundException::new);
		this.logger.trace("Le user {}-{} a demandé son match ...",
				user.getId(),
				user.getPseudo());
		
		if (user.getCurrentMatch() == null) {
			this.logger.trace("Le user {}-{} n'est dans aucun match !",
					user.getId(),
					user.getPseudo());
			throw new PlayerNotInAMatchException();
		}
		
		Match match = this.daoMatch.findById(user.getCurrentMatch().getId()).orElseThrow(MatchNotFoundException::new);
		this.logger.trace("Le user {}-{} a récupéré son match",
				user.getId(),
				user.getPseudo());
		
		return match;
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
		
		user.setCurrentMatch(match);
		
		this.daoMatch.save(match);
		this.logger.trace("Le match d'id {} a été créé à la demande de ({}){}.",
				match.getId(),
				user.getId(),
				user.getPseudo());
		
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
		
		this.logger.trace("Le user {}-{} a rejoint le match {}",
				user.getId(),
				user.getPseudo(),
				match.getId());
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
		
		this.logger.trace("Le user {}-{} a quitté le match {}",
				user.getId(),
				user.getPseudo(),
				match.getId());	
		
		if (match.getPlayerList().isEmpty()) {
			switch (match.getState()) {
				case TERMINATED:
					this.daoCard.clearMatch(match.getId());
					this.logger.trace("Les cartes du match {} ont été clear", match.getId());
					this.daoCard.deleteCardMatch(match.getId());
					this.logger.trace("Les cartes du match {} ont été supprimées", match.getId());
					break;
				case PENDING:
					this.daoMatch.deleteById(match.getId());
					this.logger.trace("Le match {} a été effacé", match.getId());
					break;
				case PLAYING:
					this.logger.trace("Erreur switch");
					throw new MatchNotLeavableException();
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
		
		Random rand = new SecureRandom();
		int randomNum = rand.nextInt(match.getPlayerList().size());
		match.setCurrentPlayer(match.getPlayerList().get(randomNum));
		match.setLastPlayer(match.getPlayerList().get(randomNum));
		match.setState(MatchState.PLAYING);
		
		this.daoCard.saveAll(match.getCardList());
		this.daoMatch.save(match);
				
		this.logger.trace("Le match {} a été généré.", match.getId());
		this.logger.trace("Les cartes du match {} de  à {} a été généré",
			match.getId(),
			match.getCardList().get(0).getId(),
			match.getCardList().get(match.getCardList().size() - 1).getId());
	}

	@PostMapping("/play")
	private void playCard(@RequestBody Card cardRequest) {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = this.daoMatch.findById(user.getCurrentMatch().getId()).orElseThrow(MatchNotFoundException::new);
		Card card = this.daoCard.findById(cardRequest.getId()).orElseThrow(CardNotFoundException::new);
		
		if (match.getState() != MatchState.PLAYING) {throw new MatchNotPlayingException();}
		if (user.getId() != match.getCurrentPlayer().getId()) {throw new PlayerNotYourTurnException();}
		if (card.getOwner().getId() == match.getLastPlayer().getId()) {throw new LastPlayerCardException();}
		if (card.getOwner().getId() == match.getCurrentPlayer().getId()) {throw new CurrentPlayerCardException();}
		if (card.getState() != CardState.HIDDEN) {throw new CardAlreadyRevealedException();}
		
		card.setState(CardState.REVEALED);
		this.logger.trace("La carte révélée par le joueur {} était une carte {}",
				user.getPseudo(),
				card.getType());
		match.setLastPlayer(match.getCurrentPlayer());
		match.setCurrentPlayer(card.getOwner());
		
		switch (card.getType()) {
			case DIFFUSE:
				match = this.matchService.switchDiffuseCase(match);
				break;
			case BAIT:
				match = this.matchService.switchBaitCase(match);
				break;
			case BOMB:
				match = this.matchService.switchBombCase(match);
				break;
			default:
				throw new TransactionErrorException();
		}
		
		this.daoMatch.save(match);
	}


}
