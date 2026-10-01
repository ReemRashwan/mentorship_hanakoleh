package com.mentorship.hanakoleh.domain.order.exception;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;

import java.time.Duration;

public class SlaNotBreachedException extends RuntimeException {
    public SlaNotBreachedException(Long orderId, OrderFinalStatus status, Duration elapsed, Duration threshold) {
        super(String.format(
                "SLA has not been breached for order ID %d in status %s. Elapsed time (%d mins) is below the required threshold of %d mins.",
                orderId, status, elapsed.toMinutes(), threshold.toMinutes()
        ));
    }
}
