package fr.xitoiz.timebomb.match;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import fr.xitoiz.timebomb.card.Card;
import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.CardType;
import fr.xitoiz.timebomb.enums.MatchState;
import fr.xitoiz.timebomb.enums.PlayerRole;
import fr.xitoiz.timebomb.projection.Views;
import fr.xitoiz.timebomb.user.User;

@Entity
@Table(name = "[MATCH]") // (name = "MATCH") est interdit par MySQL
public class Match {

// ----- Parameters ----- //
	@Id
    @Column(name = "MATCH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Common.class)
	private int id;
	
	@Column(name = "MATCH_NAME", length = 25, nullable = false)
	@JsonView(Views.Common.class)
	private String name;
	
	@Column(name = "MATCH_STATE", nullable = false)
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Common.class)
	private MatchState state = MatchState.PENDING;
	
	@OneToMany(mappedBy = "match")
	@JsonView(Views.Match.class)
	private List<Card> cardList;
	
	@OneToMany(mappedBy = "currentMatch")
	@JsonView(Views.MatchSummary.class)
	private List<User> playerList;
	
	@OneToOne
	@JoinColumn(name = "MATCH_LAST_PLAYER_ID")
	@JsonView(Views.Match.class)
	private User lastPlayer;
	
	@OneToOne
	@JoinColumn(name = "MATCH_CURRENT_PLAYER_ID")
	@JsonView(Views.Match.class)
	private User currentPlayer;
	
	@JoinColumn(name = "MATCH_WINNER_ROLE")
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Common.class)
	private PlayerRole winnerRole;
// ----- Parameters ----- //	

	
// ----- Constructor ----- //
	public Match(String name, MatchState state, List<Card> cardList, List<User> playerList, User lastPlayer,
			User currentPlayer, PlayerRole winnerRole) {
		this.name = name;
		this.state = state;
		this.cardList = cardList;
		this.playerList = playerList;
		this.lastPlayer = lastPlayer;
		this.currentPlayer = currentPlayer;
		this.winnerRole = winnerRole;
	}

	public Match() {
	}
// ----- Constructor ----- //

	
// ----- Getter & Setter ----- //
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
	
	public void addPlayer(User user) {
		if (this.playerList == null) {this.playerList = new ArrayList<>();}
		this.playerList.add(user);
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
// ----- Getter & Setter ----- //
	
	
// ----- Methods ----- //
	public Match generatePlayerRole() {
		List<User> listOfPlayer = this.getPlayerList();
		ArrayList<PlayerRole> listOfPlayerRole = new ArrayList<>();
		
		listOfPlayerRole.add(PlayerRole.MORIARTY);
		listOfPlayerRole.add(PlayerRole.MORIARTY);
		listOfPlayerRole.add(PlayerRole.SHERLOCK);
		listOfPlayerRole.add(PlayerRole.SHERLOCK);
		listOfPlayerRole.add(PlayerRole.SHERLOCK);
		
		if (listOfPlayer.size() > 5) {
			listOfPlayerRole.add(PlayerRole.SHERLOCK);
			if (listOfPlayer.size() > 6) {
				listOfPlayerRole.add(PlayerRole.MORIARTY);
				listOfPlayerRole.add(PlayerRole.SHERLOCK);
			}
		}
				
		Collections.shuffle(listOfPlayerRole);
		
		for (int i = 0; i < listOfPlayer.size(); i++) {
			listOfPlayer.get(i).setRole(listOfPlayerRole.get(i));
		}
		
		return this;
	}

	public void generateFirstPlayer() {
		Random rand = new SecureRandom();
		int randomNum = rand.nextInt(this.getPlayerList().size());
		
		this.setCurrentPlayer(this.getPlayerList().get(randomNum));
		this.setLastPlayer(this.getPlayerList().get(randomNum));
	}

	public boolean isDefuseLeft() {
		List<Card> listOfCard = this.getCardList();
		
		for (Card card: listOfCard) {
			if (card.getType() == CardType.DIFFUSE && card.getState() == CardState.HIDDEN) {
				return true;
			}
		}
		return false;
	}

	public boolean isRoundOver() {
		List<Card> listOfCard = this.getCardList();
		int nbCardRevealedThisTurn = 0;
		int nbPlayer = this.getPlayerList().size();
		
		for (Card card: listOfCard) {
			if (card.getOwner() != null && card.getState() == CardState.REVEALED) {
				nbCardRevealedThisTurn = nbCardRevealedThisTurn + 1;
			}
		}
		return nbCardRevealedThisTurn == nbPlayer;
	}

	public boolean isMatchOver() {
		List<Card> listOfCard = this.getCardList();
		int nbCardHidden = 0;
		int nbPlayer = this.getPlayerList().size();
		
		for (Card card: listOfCard) {
			if (card.getState() == CardState.HIDDEN) {
				nbCardHidden = nbCardHidden + 1;
			}
		}
		return nbCardHidden == nbPlayer;
	}
// ----- Methods ----- //

}
