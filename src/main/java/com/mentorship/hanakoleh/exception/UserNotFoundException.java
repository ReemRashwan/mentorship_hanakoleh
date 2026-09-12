package com.mentorship.hanakoleh.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userNotAuthenticated) {
        super(userNotAuthenticated);
    }
}
