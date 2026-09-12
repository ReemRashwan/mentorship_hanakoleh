package com.mentorship.hanakoleh.domain.order.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Order not found: " + orderId);
        }
    public OrderNotFoundException(String message) {
        super(message);
    }
}
