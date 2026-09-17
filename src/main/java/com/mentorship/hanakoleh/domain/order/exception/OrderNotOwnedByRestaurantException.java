package com.mentorship.hanakoleh.domain.order.exception;

public class OrderNotOwnedByRestaurantException extends RuntimeException {
    public OrderNotOwnedByRestaurantException(Long activeOrderId, Integer authenticatedRestaurantId) {
    }
}
