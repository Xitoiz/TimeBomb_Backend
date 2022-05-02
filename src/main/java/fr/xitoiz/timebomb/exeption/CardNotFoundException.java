package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "CARD NOT FOUND")
public class CardNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public CardNotFoundException() {
		System.out.println("Error returned : CARD NOT FOUND");
	}
}