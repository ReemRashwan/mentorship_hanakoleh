package com.mentorship.hanakoleh.domain.payment.exception;

public class PaymentMethodNotSupportedException extends RuntimeException {
    public PaymentMethodNotSupportedException(String message) {
        super(message);
    }
}
