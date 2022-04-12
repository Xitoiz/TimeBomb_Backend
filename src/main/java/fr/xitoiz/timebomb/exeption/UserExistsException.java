package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "LOGIN EXISTS")
public class UserExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}