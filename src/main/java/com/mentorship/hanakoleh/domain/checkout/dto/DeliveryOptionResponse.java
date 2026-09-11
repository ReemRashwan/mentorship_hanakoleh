package com.mentorship.hanakoleh.domain.checkout.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import java.math.BigDecimal;

public record DeliveryOptionResponse(
        Integer configId,
        OrderDeliveryOption deliveryOption,
        boolean pickup,
        BigDecimal deliveryFee,
        int estimatedMinutes,
        BigDecimal distanceKm) {   // null when picked up in person
}