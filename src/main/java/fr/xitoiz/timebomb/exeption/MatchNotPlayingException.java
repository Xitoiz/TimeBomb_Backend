package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "MATCH NOT PLAYING")
public class MatchNotPlayingException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MatchNotPlayingException() {
		System.out.println("Error returned : MATCH NOT PLAYING");
	}
}
