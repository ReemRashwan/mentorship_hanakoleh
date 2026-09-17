package com.mentorship.hanakoleh.domain.restaurant.exception;

public class MenuItemUnavailableException extends RuntimeException {
    public MenuItemUnavailableException(String message) {
        super(message);
    }
}
