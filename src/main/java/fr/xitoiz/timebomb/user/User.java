package fr.xitoiz.timebomb.user;

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

import com.fasterxml.jackson.annotation.JsonView;

import fr.xitoiz.timebomb.enums.AccountType;
import fr.xitoiz.timebomb.enums.PlayerRole;
import fr.xitoiz.timebomb.match.Match;
import fr.xitoiz.timebomb.projection.Views;

@Entity
@Table(name = "USER")
public class User {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Common.class)
	private int id;
	
    @Column(name = "USER_PSEUDO", length = 25, nullable = false, unique=true)
    @JsonView(Views.Common.class)
	private String pseudo;
	
	@Column(name = "USER_LOGIN", length = 25, nullable = false)
	private String login;
	
	@Column(name = "USER_PASSWORD", length = 250, nullable = false)
	private String password;
	
    @Column(name = "USER_SYSTEM_ROLE")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Common.class)
    private AccountType accountType;
    
    @ManyToOne
    @JoinColumn(name = "USER_MATCH_ID")
    @JsonView(Views.User.class)
	private Match currentMatch;
	
    @Column(name = "USER_MATCH_ROLE")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Common.class)
	private PlayerRole playerRole;

    
	public User(Integer id, String pseudo, String login, String password, Match currentMatch, PlayerRole playerRole, AccountType accountType) {
		this.id = id;
		this.pseudo = pseudo;
		this.login = login;
		this.password = password;
		this.currentMatch = currentMatch;
		this.playerRole = playerRole;
		this.accountType = accountType;
	}
	
	public User() {
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPseudo() {
		return this.pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Match getCurrentMatch() {
		return this.currentMatch;
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}

	public PlayerRole getPlayerRole() {
		return this.playerRole;
	}

	public void setRole(PlayerRole playerRole) {
		this.playerRole = playerRole;
	}

	public AccountType getAccountType() {
		return this.accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
}
