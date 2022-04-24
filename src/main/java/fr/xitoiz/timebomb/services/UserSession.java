package fr.xitoiz.timebomb.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import fr.xitoiz.timebomb.enums.AccountType;

@Service
@SessionScope
public class UserSession {
	private int id;
	private String pseudo;
	private AccountType type;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	
	public AccountType getAccountType() {
		return this.type; 
	}
	
	public void setAccountType(AccountType type) {
		this.type = type;
	}
}