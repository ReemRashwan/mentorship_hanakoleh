package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import java.time.OffsetDateTime;

public record OrderStatusWebSocketMessage(
        Long orderId,
        OrderFinalStatus previousStatus,
        OrderFinalStatus status,
        OffsetDateTime changedAt) {
}
