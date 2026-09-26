package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest (
        @NotBlank
        OrderFinalStatus nextOrderStatus,
        @NotNull
        Integer restaurantId,
        @NotBlank
        String eventTrigger,
        boolean cashPaymentCollected,
        String notes
) {
}