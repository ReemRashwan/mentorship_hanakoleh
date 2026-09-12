package com.mentorship.hanakoleh.domain.checkout.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException(String message) {
        super(message);
    }
}
