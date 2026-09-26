package com.mentorship.hanakoleh.domain.order.exception;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import jakarta.validation.constraints.NotNull;

public class InvalidOrderTransitionException extends RuntimeException {
    public InvalidOrderTransitionException(@NotNull OrderFinalStatus currentStatus, OrderFinalStatus nextOrderStatus) {
    super("Invalid Order Transition State: " + currentStatus + " -> " + nextOrderStatus);
    }
}
