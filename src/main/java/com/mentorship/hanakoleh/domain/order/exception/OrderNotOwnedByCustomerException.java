package com.mentorship.hanakoleh.domain.order.exception;

public class OrderNotOwnedByCustomerException extends RuntimeException {
    public OrderNotOwnedByCustomerException(Long orderId, Integer cancelingActorUserId) {
    }
}
