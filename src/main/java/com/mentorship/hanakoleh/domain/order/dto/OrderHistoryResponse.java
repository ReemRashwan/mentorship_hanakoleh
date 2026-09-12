package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderHistoryResponse (
    Long orderId,
    String restaurantName,
    OrderFinalStatus status,
    BigDecimal totalAmount,
    String currencyCode,
    long itemLineCount,
    OffsetDateTime createdAt

){}
