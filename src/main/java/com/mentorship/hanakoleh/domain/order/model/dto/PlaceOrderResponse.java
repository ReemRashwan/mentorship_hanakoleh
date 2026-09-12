package com.mentorship.hanakoleh.domain.order.model.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;

public record PlaceOrderResponse(Long orderId, OrderFinalStatus status) {}