package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "NOT THE LAST PLAYER")
public class NotTheLastPlayerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
