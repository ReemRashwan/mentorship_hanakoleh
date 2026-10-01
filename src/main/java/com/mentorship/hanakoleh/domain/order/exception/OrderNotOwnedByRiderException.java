package com.mentorship.hanakoleh.domain.order.exception;

public class OrderNotOwnedByRiderException extends RuntimeException {
    public OrderNotOwnedByRiderException(Long activeOrderId, Long authenticatedRiderId) {
        super("Order "+activeOrderId+" not owned by rider:  -> "+authenticatedRiderId);
    }
}
