package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;

public record PlaceOrderResponse(Long orderId, OrderFinalStatus status) {}