package fr.xitoiz.timebomb.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private CardType type;
	
	@Column(name = "CARD_REVEALED")
	private Boolean revealed = false;

	public Card(Integer id, Match match, User owner, CardType type, Boolean revealed) {
		this.id = id;
		this.match = match;
		this.owner = owner;
		this.type = type;
		this.revealed = revealed;
	}
	
	public Card() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	public Boolean getRevealed() {
		return revealed;
	}

	public void setRevealed(Boolean revealed) {
		this.revealed = revealed;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", match=" + match + ", owner=" + owner + ", type=" + type + ", revealed=" + revealed
				+ "]";
	}
	
}
