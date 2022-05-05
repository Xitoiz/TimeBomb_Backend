package fr.xitoiz.timebomb.match_result;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import fr.xitoiz.timebomb.enums.PlayerRole;
import fr.xitoiz.timebomb.match.Match;
import fr.xitoiz.timebomb.projection.Views;
import fr.xitoiz.timebomb.user.User;

@Entity
@Table(name = "MATCH_RESULT")
public class MatchResult {

	@Id
    @Column(name = "MATCH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Common.class)
	private int id;
	
	@Column(name = "MATCH_WINNERS_ROLE")
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Common.class)
	private PlayerRole winnerRole;
	
	@ManyToMany
	@Column(name = "MATCH_WINNERS")
	@JsonView(Views.MatchSummary.class)
	private List<User> winners = new ArrayList<>();
	
	@ManyToMany
	@Column(name = "MATCH_LOOSERS")
	@JsonView(Views.MatchSummary.class)
	private List<User> loosers = new ArrayList<>();
	
	@Column(name = "MATCH_WIN_CONDITION", length = 250)
	@JsonView(Views.Common.class)
	private String winCondition;

	public MatchResult(Match match, String winCondition) {
		this.winnerRole = match.getWinnerRole();
		this.winCondition = winCondition;
		this.addWinners(match);
	}

	public MatchResult(int id, PlayerRole winnerRole, List<User> winners, List<User> loosers, String winCondition) {
		this.id = id;
		this.winnerRole = winnerRole;
		this.winners = winners;
		this.loosers = loosers;
		this.winCondition = winCondition;
	}

	public MatchResult() {}

	private void addWinners(Match match) {
		for (User player: match.getPlayerList()) {
			if (player.getPlayerRole() == match.getWinnerRole()) {
				this.winners.add(player);
			}
			else {
				this.loosers.add(player);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PlayerRole getWinnerRole() {
		return winnerRole;
	}

	public void setWinnerRole(PlayerRole winnerRole) {
		this.winnerRole = winnerRole;
	}

	public List<User> getWinners() {
		return winners;
	}

	public void setWinners(List<User> winners) {
		this.winners = winners;
	}

	public List<User> getLoosers() {
		return loosers;
	}

	public void setLoosers(List<User> loosers) {
		this.loosers = loosers;
	}

	public String getWinCondition() {
		return winCondition;
	}

	public void setWinCondition(String winCondition) {
		this.winCondition = winCondition;
	}	
	
}
