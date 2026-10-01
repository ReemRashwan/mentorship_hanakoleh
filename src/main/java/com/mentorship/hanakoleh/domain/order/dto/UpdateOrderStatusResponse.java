package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;

public record UpdateOrderStatusResponse(Long orderId, OrderFinalStatus status) {}