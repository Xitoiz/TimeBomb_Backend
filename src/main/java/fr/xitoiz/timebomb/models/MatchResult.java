package fr.xitoiz.timebomb.models;

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

import fr.xitoiz.timebomb.enums.PlayerRole;

@Entity
@Table(name = "MATCH_RESULT")
public class MatchResult {

	@Id
    @Column(name = "MATCH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "MATCH_WINNERS_ROLE")
	@Enumerated(EnumType.STRING)
	private PlayerRole winnerRole;
	
	@ManyToMany
	@Column(name = "MATCH_WINNERS")
	private List<User> winners;
	
	@ManyToMany
	@Column(name = "MATCH_LOOSERS")
	private List<User> loosers;

	public MatchResult(int id, PlayerRole winnerRole, List<User> winners, List<User> loosers) {
		super();
		this.id = id;
		this.winnerRole = winnerRole;
		this.winners = winners;
		this.loosers = loosers;
	}

	public MatchResult() {
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
	
	
}
