package com.mentorship.hanakoleh.domain.user.exception;


public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
