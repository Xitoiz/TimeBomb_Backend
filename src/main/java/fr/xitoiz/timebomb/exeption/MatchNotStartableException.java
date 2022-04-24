package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "MATCH NOT STARTABLE")
public class MatchNotStartableException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MatchNotStartableException() {
		System.out.println("Error returned : MATCH NOT STARTABLE");
	}
}
