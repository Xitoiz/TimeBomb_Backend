package fr.xitoiz.timebomb.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class UserSession {
	private int id;
	private String pseudo;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setName(String pseudo) {
		this.pseudo = pseudo;
	}
}