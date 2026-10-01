package com.mentorship.hanakoleh.domain.checkout.exception;

import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;

public class PaymentNotSatisfiedException extends RuntimeException {
    public PaymentNotSatisfiedException(Long orderId, OrderPaymentMethod method, OrderPaymentStatus status) {
        super(String.format("Order %d cannot be confirmed: paymentMethod=%s, paymentStatus=%s", orderId, method, status));
    }
}