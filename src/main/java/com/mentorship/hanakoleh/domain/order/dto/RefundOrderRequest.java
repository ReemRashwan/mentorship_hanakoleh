package com.mentorship.hanakoleh.domain.order.dto;


public record RefundOrderRequest(
        Long orderId,
        Integer restaurantId,
        String reason,
        String notes) {
}
