package fr.xitoiz.timebomb.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.xitoiz.timebomb.enums.CardState;
import fr.xitoiz.timebomb.enums.CardType;

@Entity
@Table(name = "CARD")
public class Card {
	
	@Id
    @Column(name = "CARD_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "CARD_MATCH_ID")
	private Match match;
	
	@ManyToOne
	@JoinColumn(name = "CARD_OWNER_ID")
	private User owner;
	
	@Column(name = "CARD_TYPE")
	@Enumerated(EnumType.STRING)
	private CardType type;
	
	@Column(name = "CARD_STATE")
	@Enumerated(EnumType.STRING)
	private CardState state = CardState.HIDDEN;

	public Card(Integer id, Match match, User owner, CardType type, CardState state) {
		this.id = id;
		this.match = match;
		this.owner = owner;
		this.type = type;
		this.state = state;
	}
	
	public Card() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Match getMatch() {
		return this.match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public User getOwner() {
		return this.owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public CardType getType() {
		return this.type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	public CardState getState() {
		return this.state;
	}

	public void setState(CardState state) {
		this.state = state;
	}
	
}
