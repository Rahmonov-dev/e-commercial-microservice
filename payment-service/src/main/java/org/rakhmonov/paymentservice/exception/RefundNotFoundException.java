package org.rakhmonov.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RefundNotFoundException extends RuntimeException {
    public RefundNotFoundException(String message) {
        super(message);
    }

    public RefundNotFoundException(Long id) {
        super("Refund not found with ID: " + id);
    }
}


