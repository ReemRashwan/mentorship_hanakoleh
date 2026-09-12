package com.mentorship.hanakoleh.domain.restaurant.exception;

public class InvalidRestaurantIdException extends RuntimeException {
    public InvalidRestaurantIdException(String message) {
        super(message);
    }
}
