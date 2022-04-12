package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED, reason = "TRANSACTION FAILED")
public class TransactionErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}