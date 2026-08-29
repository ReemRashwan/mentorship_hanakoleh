package com.mentorship.hanakoleh.exception;

public class UserTokenNotFoundException extends RuntimeException {
    public UserTokenNotFoundException(String message) {
        super(message);
    }
}
