package com.mentorship.hanakoleh.domain.restaurant.exception;

public class MenuItemNotOrderableException extends RuntimeException {

    public MenuItemNotOrderableException(String message) {
        super(message);
    }
}
