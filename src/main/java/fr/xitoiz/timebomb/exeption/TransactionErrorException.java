package fr.xitoiz.timebomb.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Error in the Transaction")
public class TransactionErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public TransactionErrorException() {
		System.out.println("Error returned : Error in the Transaction");
	}
}
