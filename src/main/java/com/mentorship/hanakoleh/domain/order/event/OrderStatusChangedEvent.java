package com.mentorship.hanakoleh.domain.order.event;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import java.time.OffsetDateTime;

public record OrderStatusChangedEvent(
        Long orderId,
        Integer customerId,
        OrderFinalStatus previousStatus,
        OrderFinalStatus currentStatus,
        OffsetDateTime changedAt) {
}
