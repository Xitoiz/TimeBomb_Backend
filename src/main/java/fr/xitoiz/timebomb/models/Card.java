/**
 * 
 */
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
}
