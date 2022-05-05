package fr.xitoiz.timebomb.match;

import java.util.List;

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

import fr.xitoiz.timebomb.card.Card;
import fr.xitoiz.timebomb.projection.Views;
import fr.xitoiz.timebomb.user.UserSession;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/match")
public class MatchApiController {
	public final Logger logger = LoggerFactory.getLogger(MatchApiController.class);
	
	@Autowired
	public UserSession userSession;
	
	@Autowired
	public MatchService matchService;
	
	@Autowired
	private MatchDAO daoMatch;
	
	@GetMapping
	@JsonView(Views.MatchSummary.class)
	public List<Match> getAllMatch() {
		return this.daoMatch.findAll();
	}
	
	@GetMapping("/pending")
	@JsonView(Views.MatchSummary.class)
	public List<Match> getAllPendingMatch() {
		return this.matchService.getAllPendingMatch();
	}
	
	@GetMapping("/playing")
	@JsonView(Views.MatchSummary.class)
	public List<Match> getAllPlayingMatch() {
		return this.matchService.getAllPlayingMatch();
	}
	
	@PostMapping("/create")
	public void createMatch(@Valid @RequestBody Match match) {
		this.matchService.createMatch(match);
	}
	
	@PostMapping("/join")
	public void joinMatch(@RequestBody Match matchRequested) {
		this.matchService.joinMatch(matchRequested);
	}
	
	@PostMapping("/leave")
	public void leaveMatch(@RequestBody Match matchRequested) {
		this.matchService.leaveMatch(matchRequested);
	}
	
	@GetMapping("/start")
	@Transactional
	public void startMatch() {
		this.matchService.startMatch();
	}

	@PostMapping("/play")
	public void playCard(@RequestBody Card cardRequest) {
		this.matchService.playCard(cardRequest);
	}


}
