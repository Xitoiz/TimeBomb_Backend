package fr.xitoiz.timebomb.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.xitoiz.timebomb.enums.PlayerRole;

@Entity
@Table(name = "USER")
public class User {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Column(name = "USER_PSEUDO", length = 25, nullable = false)
	private String pseudo;
	
	@Column(name = "USER_LOGIN", length = 25, nullable = false)
	private String login;
	
	@Column(name = "USER_PASSWORD", length = 250, nullable = false)
	private String password;
	
    @ManyToOne
    @JoinColumn(name = "USER_MATCH_ID")
	private Match currentMatch;
	
    @Column(name = "USER_MATCH_ROLE")
	private PlayerRole role;


	public User() {
	}
 
	public User(Integer id, String pseudo, String login, String password, Match currentMatch, PlayerRole role) {
		this.id = id;
		this.pseudo = pseudo;
		this.login = login;
		this.password = password;
		this.currentMatch = currentMatch;
		this.role = role;
	}
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Match getCurrentMatch() {
		return currentMatch;
	}

	public void setCurrentMatch(Match currentMatch) {
		this.currentMatch = currentMatch;
	}

	public PlayerRole getRole() {
		return role;
	}

	public void setRole(PlayerRole role) {
		this.role = role;
	}
    
}
