package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CurrentOrderResponse(
        Long orderId,
        String restaurantName,
        OrderFinalStatus status,
        OffsetDateTime createdAt,
        BigDecimal totalAmount,
        String currencyCode
) {
}