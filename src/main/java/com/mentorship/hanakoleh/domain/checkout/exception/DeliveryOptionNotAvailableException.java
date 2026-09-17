package com.mentorship.hanakoleh.domain.checkout.exception;

public class DeliveryOptionNotAvailableException extends RuntimeException {

    public DeliveryOptionNotAvailableException(String message) {
        super(message);
    }
}