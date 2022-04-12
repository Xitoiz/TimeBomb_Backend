package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "NOT THE OWNER")
public class NotTheOwnerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
