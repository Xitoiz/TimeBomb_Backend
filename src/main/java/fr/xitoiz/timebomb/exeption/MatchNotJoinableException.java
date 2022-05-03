package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "MATCH NOT PENDING")
public class MatchNotJoinableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}
