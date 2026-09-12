package com.mentorship.hanakoleh.domain.checkout.exception;

public class OutOfDeliveryZoneException extends RuntimeException {

    public OutOfDeliveryZoneException(String message) {
        super(message);
    }
}