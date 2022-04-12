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

@Entity
@Table(name = "MATCH")
public class Match {

	@Id
    @Column(name = "MATCH_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "MATCH_OWNER_NAME", length = 25, nullable = false)
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
	private PlayerRole winnerRole;
	
	@ManyToMany
	@Column(name = "MATCH_WINNERS")
	private List<User> winners;
	
	@ManyToMany
	@Column(name = "MATCH_LOOSERS")
	private List<User> loosers;

}
