package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "MATCH NOT FOUND")
public class MatchNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public MatchNotFoundException() {
		System.out.println("Error returned : MATCH NOT FOUND");
	}
}