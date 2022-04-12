package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "YOU ARE NOT IN THAT MATCH")
public class PlayerNotInThisMatchException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
