package fr.xitoiz.timebomb.projection;

import com.fasterxml.jackson.annotation.JsonView;

import fr.xitoiz.timebomb.card.Card;
import fr.xitoiz.timebomb.enums.AccountType;
import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.CardType;
import fr.xitoiz.timebomb.enums.PlayerRole;
import fr.xitoiz.timebomb.match.Match;
import fr.xitoiz.timebomb.user.User;

public class MatchProjection {

	@JsonView(Views.Match.class)
	private Match match;
	
	@JsonView(Views.Match.class)
	private int nbDiffuse = 0;
	
	@JsonView(Views.Match.class)
	private boolean isBombInHand = false;
	
	@JsonView(Views.Match.class)
	private PlayerRole matchRole;

	
	public MatchProjection(Match match, User user) {
		this.match = match;
		this.matchRole = user.getPlayerRole();
		this.getHandResume(match, user);
		if (user.getAccountType() == AccountType.PLAYER) {this.cleanCard(match);}
	}
	
	public MatchProjection(Match match) {
		this.match = match;
		this.matchRole = null;
		this.nbDiffuse = 0;
		this.isBombInHand = false;
		this.cleanCard(match);
	}
	
	
	private void getHandResume(Match match, User user) {
		for (Card card: match.getCardList()) {
			if (card.getType() == CardType.DIFFUSE && card.getOwner().getId() == user.getId()) {
				this.nbDiffuse = this.nbDiffuse + 1;
			}
			if (card.getType() == CardType.BOMB && card.getOwner().getId() == user.getId()) {
				this.isBombInHand = true;
			}
		}
	}
	
	private void cleanCard(Match match) {
		for (Card card: match.getCardList()) {
			if (card.getState() == CardState.HIDDEN) {card.setType(null);}
		}
	}

}
