package fr.xitoiz.timebomb.match_result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonView;

import fr.xitoiz.timebomb.match.Match;
import fr.xitoiz.timebomb.projection.Views;

@Service
public class MatchResultService {

	@Autowired
	private MatchResultDAO daoMatchResult;
	
	@JsonView(Views.Match.class)
	public void save(Match match, String winCondition) {
		MatchResult matchResult = new MatchResult(match, winCondition);
		
		this.daoMatchResult.save(matchResult);
	}

	
	
}
