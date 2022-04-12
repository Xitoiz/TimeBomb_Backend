package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "ALREADY IN A MATCH")
public class PlayerAlreadyInAMatchException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}