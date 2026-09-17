package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;

public record UpdateOrderStatusRequest (
        Integer restaurantId,
        Integer deliveryAddressId,
        OrderPaymentMethod paymentMethod,
        OrderPaymentStatus paymentStatusStatus
) {}