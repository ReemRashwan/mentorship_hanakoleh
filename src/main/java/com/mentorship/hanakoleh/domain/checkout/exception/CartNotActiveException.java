package com.mentorship.hanakoleh.domain.checkout.exception;

public class CartNotActiveException extends RuntimeException {

    public CartNotActiveException(String message) {
        super(message);
    }
}
