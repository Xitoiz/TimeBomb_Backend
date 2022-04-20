package fr.xitoiz.timebomb.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.xitoiz.timebomb.enums.MatchState;
import fr.xitoiz.timebomb.enums.PlayerRole;
import fr.xitoiz.timebomb.services.UserSession;

@Entity
@Table(name = "[MATCH]") // (name = "MATCH") est interdit par MySQL
public class Match {

	@Id
    @Column(name = "MATCH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "MATCH_NAME", length = 25, nullable = false)
	private String name;
	
	@Column(name = "MATCH_STATE", nullable = false)
	@Enumerated(EnumType.STRING)
	private MatchState state = MatchState.PENDING;
	
	@OneToMany(mappedBy = "match")
	private List<Card> cardList;
	
	@OneToMany(mappedBy = "currentMatch")
	private List<User> playerList;
	
	@OneToOne
	@JoinColumn(name = "MATCH_LAST_PLAYER_ID")
	private User lastPlayer;
	
	@OneToOne
	@JoinColumn(name = "MATCH_CURRENT_PLAYER_ID")
	private User currentPlayer;
	
	@Column(name = "MATCH_WINNERS_ROLE")
	@Enumerated(EnumType.STRING)
	private PlayerRole winnerRole;
	
	@ManyToMany
	@Column(name = "MATCH_WINNERS")
	private List<User> winners;
	
	@ManyToMany
	@Column(name = "MATCH_LOOSERS")
	private List<User> loosers;

	public Match(int id, String name, MatchState state, List<Card> cardList, List<User> playerList, User lastPlayer,
			User currentPlayer, PlayerRole winnerRole, List<User> winners, List<User> loosers) {
		this.id = id;
		this.name = name;
		this.state = state;
		this.cardList = cardList;
		this.playerList = playerList;
		this.lastPlayer = lastPlayer;
		this.currentPlayer = currentPlayer;
		this.winnerRole = winnerRole;
		this.winners = winners;
		this.loosers = loosers;
	}
	
	public Match() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MatchState getState() {
		return state;
	}

	public void setState(MatchState state) {
		this.state = state;
	}

	public List<Card> getCardList() {
		return cardList;
	}

	public void setCardList(List<Card> cardList) {
		this.cardList = cardList;
	}

	public List<User> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<User> playerList) {
		this.playerList = playerList;
	}
	
	public void addPlayerToList(User player) {
		this.playerList.add(player);
	}
	
	public void addPlayerToList(UserSession userSession) {
		User player = new User();
		player.setId(userSession.getId());
		this.playerList.add(player);
	}

	public User getLastPlayer() {
		return lastPlayer;
	}

	public void setLastPlayer(User lastPlayer) {
		this.lastPlayer = lastPlayer;
	}

	public User getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(User currentPlayer) {
		this.currentPlayer = currentPlayer;
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

	@Override
	public String toString() {
		return "Match [id=" + id + ", name=" + name + ", state=" + state + ", cardList=" + cardList + ", playerList="
				+ playerList + ", lastPlayer=" + lastPlayer + ", currentPlayer=" + currentPlayer + ", winnerRole="
				+ winnerRole + ", winners=" + winners + ", loosers=" + loosers + "]";
	}

}
