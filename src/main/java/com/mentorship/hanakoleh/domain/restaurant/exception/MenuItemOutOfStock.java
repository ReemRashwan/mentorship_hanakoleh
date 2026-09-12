package com.mentorship.hanakoleh.domain.restaurant.exception;

public class MenuItemOutOfStock extends RuntimeException {
    public MenuItemOutOfStock(String message) {
        super(message);
    }
}
