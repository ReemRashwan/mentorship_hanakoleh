package com.mentorship.hanakoleh.domain.user.exception;

public class UserAccessDeniedException extends RuntimeException{
    public UserAccessDeniedException(String message){
        super(message);
    }
}
