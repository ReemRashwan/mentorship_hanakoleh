package com.mentorship.hanakoleh.domain.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userNotAuthenticated) {
        super(userNotAuthenticated);
    }
}
