package com.mentorship.hanakoleh.domain.checkout.exception;

public class InvalidDeliveryAddressException extends RuntimeException {

    public InvalidDeliveryAddressException(String message) {
        super(message);
    }
}