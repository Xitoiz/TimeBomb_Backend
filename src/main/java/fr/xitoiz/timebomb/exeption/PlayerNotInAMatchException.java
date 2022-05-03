package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "PLAYER NOT IN A MATCH")
public class PlayerNotInAMatchException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public PlayerNotInAMatchException() {
		System.out.println("Error returned : PLAYER NOT IN A MATCH");
	}
}
