package fr.xitoiz.timebomb.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.xitoiz.timebomb.dao.IDAOCard;
import fr.xitoiz.timebomb.dao.IDAOMatch;
import fr.xitoiz.timebomb.dao.IDAOUser;
import fr.xitoiz.timebomb.enums.MatchState;
import fr.xitoiz.timebomb.exeption.MatchNotFoundException;
import fr.xitoiz.timebomb.exeption.MatchNotJoinableException;
import fr.xitoiz.timebomb.exeption.MatchNotLeavableException;
import fr.xitoiz.timebomb.exeption.MatchNotStartableException;
import fr.xitoiz.timebomb.exeption.PlayerInAMatchException;
import fr.xitoiz.timebomb.exeption.PlayerNotInAMatchException;
import fr.xitoiz.timebomb.exeption.PlayerNotInThisMatchException;
import fr.xitoiz.timebomb.exeption.TransactionErrorException;
import fr.xitoiz.timebomb.models.Match;
import fr.xitoiz.timebomb.models.User;
import fr.xitoiz.timebomb.services.MatchService;
import fr.xitoiz.timebomb.services.UserSession;

@RestController
@CrossOrigin("*")
@RequestMapping("/match")
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
	
	@PostMapping("/create")
	private void createMatch(@Valid @RequestBody Match match) {
		User user = this.daoUser.getById(this.userSession.getId());
		if (user.getCurrentMatch() != null) {throw new PlayerInAMatchException();}
		
		user.setCurrentMatch(match);
		this.daoMatch.save(match);
	
		System.out.println("Le match d'id " + match.getId() + " a été créé à la demande de " + user.getId() + " - " + user.getPseudo() + ".");
		
	}
	
	@PostMapping("/join")
	private void joinMatch(@RequestBody Match matchRequested) {
		User user = this.daoUser.getById(this.userSession.getId());
		if (user.getCurrentMatch() != null) {throw new PlayerInAMatchException();}
		
		Match match = this.daoMatch.findById(matchRequested.getId()).orElseThrow(MatchNotFoundException::new);
		if (match.getState() != (MatchState.PENDING)) {throw new MatchNotJoinableException();}
		
		user.setCurrentMatch(match);
		this.daoUser.save(user);
		
		System.out.println("Le user "  + user.getId() + "-" + user.getPseudo() + " a rejoint le match " + match.getId());
	}
	
	@PostMapping("/leave")
	private void leaveMatch(@RequestBody Match matchRequested) {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = user.getCurrentMatch();
		if (user.getCurrentMatch() == null) {throw new PlayerNotInAMatchException();}
		if (user.getCurrentMatch().getState() == MatchState.PLAYING) {throw new MatchNotLeavableException();}
		if (user.getCurrentMatch().getId() != matchRequested.getId()) {throw new PlayerNotInThisMatchException();}
		
		// Bloc pout quitter le match
		user.setCurrentMatch(null);
		this.daoUser.save(user);
		
		System.out.println("Le user "  + user.getId() + "-" + user.getPseudo() + " a quitté le match " + match.getId());	
		
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
	private void startMatch() {
		User user = this.daoUser.getById(this.userSession.getId());
		Match match = user.getCurrentMatch();
		
		if (match.getState() != MatchState.PENDING) {throw new MatchNotStartableException();}
		if (match.getPlayerList().size() > 8 || match.getPlayerList().size() < 4) {throw new MatchNotStartableException();}
		
		this.matchService.generateCard(match);
	}
}
