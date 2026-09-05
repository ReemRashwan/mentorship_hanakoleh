package com.mentorship.hanakoleh.domain.restaurant.exception;

public class CrossRestaurantConflictException extends RuntimeException {
    public CrossRestaurantConflictException(String message) {
        super(message);
    }
}
