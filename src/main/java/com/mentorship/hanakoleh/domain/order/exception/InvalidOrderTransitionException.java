package com.mentorship.hanakoleh.domain.order.exception;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import jakarta.validation.constraints.NotNull;

public class InvalidOrderTransitionException extends RuntimeException {
    public InvalidOrderTransitionException(@NotNull OrderFinalStatus finalStatus, OrderFinalStatus orderFinalStatus) {
    }
}
