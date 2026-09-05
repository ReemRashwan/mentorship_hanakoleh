package com.mentorship.hanakoleh.domain.user.exception;

public class UserTokenNotFoundException extends RuntimeException {
    public UserTokenNotFoundException(String message) {
        super(message);
    }
}
