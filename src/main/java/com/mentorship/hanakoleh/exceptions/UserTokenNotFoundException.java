package com.mentorship.hanakoleh.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserTokenNotFoundException extends RuntimeException {
    public UserTokenNotFoundException(String message) {
        super(message);
    }
}
