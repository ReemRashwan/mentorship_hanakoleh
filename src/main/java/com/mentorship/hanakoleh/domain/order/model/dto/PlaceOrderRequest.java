package com.mentorship.hanakoleh.domain.order.model.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;

public record PlaceOrderRequest(
        Integer restaurantId,
        Integer deliveryAddressId,
        OrderPaymentMethod paymentMethod,
        OrderPaymentStatus paymentStatusStatus
) {}