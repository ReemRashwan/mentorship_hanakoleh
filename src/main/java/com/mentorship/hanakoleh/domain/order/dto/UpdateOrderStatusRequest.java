package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;

public record UpdateOrderStatusRequest (
        OrderFinalStatus nextOrderStatus,
        Integer restaurantId,
        String eventTrigger,
        boolean cashPaymentCollected,
        String notes
) {
}