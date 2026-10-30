package com.mentorship.hanakoleh.domain.restaurant.exception;

public class RestaurantCategoryNotFoundException extends RuntimeException{
    public RestaurantCategoryNotFoundException(String message) {
        super(message);
    }
}
