package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "PLAYER, NOT YOUR MATCH")
public class PlayerNotInThisMatchException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public PlayerNotInThisMatchException() {
		System.out.println("Error returned : PLAYER, NOT YOUR MATCH");
	}
}
