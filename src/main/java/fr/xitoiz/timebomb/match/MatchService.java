package fr.xitoiz.timebomb.match;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.xitoiz.timebomb.card.Card;
import fr.xitoiz.timebomb.card.CardDAO;
import fr.xitoiz.timebomb.card.CardService;
import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.MatchState;
import fr.xitoiz.timebomb.enums.PlayerRole;
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
import fr.xitoiz.timebomb.match_result.MatchResultService;
import fr.xitoiz.timebomb.user.User;
import fr.xitoiz.timebomb.user.UserDAO;
import fr.xitoiz.timebomb.user.UserSession;

@Service
public class MatchService {

	private MatchService() {
	}
	
	private final Logger logger = LoggerFactory.getLogger(MatchService.class);
	
	@Autowired
	private MatchDAO daoMatch;
	
	@Autowired
	private UserDAO daoUser;
	
	@Autowired
	private CardDAO daoCard;
	
	@Autowired
	private UserSession userSession;
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private MatchResultService matchResultService;

	//@GetMapping
	public List<Match> findAllMatch() {
		return this.daoMatch.findAll();
	}
	
	//@GetMapping("/pending")
	public List<Match> findAllPendingMatch() {
		User user = this.daoUser.getById(this.userSession.getId());
		this.logger.trace("Le user ({}){} a demandé les matchs en attentes ...",
				user.getId(),
				user.getPseudo());
		
		return this.daoMatch.findAllPendingMatch();
	}
	
	//@GetMapping("/playing")
	public List<Match> findAllPlayingMatch() {
		User user = this.daoUser.getById(this.userSession.getId());
		this.logger.trace("Le user ({}){} a demandé les matchs en cours ...",
				user.getId(),
				user.getPseudo());
		
		return this.daoMatch.findAllPlayingMatch();
	}
	
	//@PostMapping("/create")
	public Match createMatch(Match match) {
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
	
	//@PostMapping("/join")
	public void joinMatch(Match matchRequested) {
		User user = this.daoUser.getById(this.userSession.getId());
		if (user.getCurrentMatch() != null) {throw new PlayerInAMatchException();}
		
		Match match = this.daoMatch.findById(matchRequested.getId()).orElseThrow(MatchNotFoundException::new);
		if (match.getState() != (MatchState.PENDING)) {throw new MatchNotJoinableException();}
		
		user.setCurrentMatch(match);
		this.daoUser.save(user);
		
		this.logger.trace("Le user ({}){} a rejoint le match {}",
				user.getId(),
				user.getPseudo(),
				match.getId());
	}
	
	//@PostMapping("/leave")
	public void leaveMatch(Match matchRequested) {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = user.getCurrentMatch();
		
		if (user.getCurrentMatch() == null) {throw new PlayerNotInAMatchException();}
		if (user.getCurrentMatch().getState() == MatchState.PLAYING) {throw new MatchNotLeavableException();}
		if (user.getCurrentMatch().getId() != matchRequested.getId()) {throw new PlayerNotInThisMatchException();}
		
		user.setCurrentMatch(null);
		this.daoUser.save(user);
		
		this.logger.trace("Le user ({}){} a quitté le match {}",
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
					this.daoMatch.delete(match);
					this.logger.trace("Le match {} a été supprimé", match.getId());
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
	
	//@GetMapping("/start")
	public void startMatch() {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = user.getCurrentMatch();
		
		if (match.getState() != MatchState.PENDING) {throw new MatchNotStartableException();}
		if (match.getPlayerList().size() > 8 || match.getPlayerList().size() < 4) {throw new MatchNotStartableException();}
		
		this.cardService.generateCard(match);
		this.cardService.distributeCards(match);
		
		match.generatePlayerRole().generateFirstPlayer();
		match.setState(MatchState.PLAYING);
		
		this.daoMatch.save(match);
				
		this.logger.trace("Le match {} a été généré.", match.getId());
		this.logger.trace("Les cartes du match {} de {} à {} a été généré",
			match.getId(),
			match.getCardList().get(0).getId(),
			match.getCardList().get(match.getCardList().size() - 1).getId());
	}
	
	//@PostMapping("/play")
	public void playCard(Card cardRequest) {
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
				this.switchDiffuseCase(match);
				break;
			case BAIT:
				this.switchBaitCase(match);
				break;
			case BOMB:
				this.switchBombCase(match);
				break;
			default:
				throw new TransactionErrorException();
		}
		
		this.daoMatch.save(match);
	}
	
	
	private void switchDiffuseCase(Match match) {
		if (!match.isDefuseLeft()) {
			this.logger.trace("Le match est terminé. La team Sherlock a gagné en désamorçant la bombe");
			this.endMatch(match, PlayerRole.SHERLOCK, "Bombe Désamorcée");
			
		}
		if (match.isMatchOver()) {
			this.logger.trace("Le match est terminé. La team Sherlock n'a pas désamorcé la bombe");
			this.endMatch(match, PlayerRole.MORIARTY, "Temps écoulé");
		}
		if(match.isRoundOver()) {
			this.cardService.distributeCards(match);
		}
	}

	private void switchBaitCase(Match match) {
		if (match.isMatchOver()) {
			this.logger.trace("Le match est terminé. La team Sherlock n'a pas désamorcé la bombe");
			this.endMatch(match, PlayerRole.MORIARTY, "Temps écoulé");
		}
		if(match.isRoundOver()) {
			this.cardService.distributeCards(match);
		}
	}
	
	private void switchBombCase(Match match) {
		this.logger.trace("Le match est terminé. La team Moriarty a fait explosé la bombe");
		this.endMatch(match, PlayerRole.MORIARTY, "Explosion de la Bombe");
	}

	private void endMatch(Match match, PlayerRole winnerRole, String reason) {
		match.setState(MatchState.TERMINATED);
		match.setWinnerRole(winnerRole);
		this.cardService.revealAllCards(match);
		this.matchResultService.save(match, reason);
	}
}
