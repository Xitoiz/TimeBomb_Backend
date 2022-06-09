package fr.xitoiz.timebomb.match_result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.xitoiz.timebomb.match.Match;

@Service
public class MatchResultService {

	@Autowired
	private MatchResultDAO daoMatchResult;
	
	public void save(Match match, String winCondition) {
		MatchResult matchResult = new MatchResult(match, winCondition);
		
		this.daoMatchResult.save(matchResult);
	}

	
	
}
