package com.mentorship.hanakoleh.domain.order.event;

import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import lombok.Data;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder

public class OrderConfirmedEvent extends OrderEvent   {
    private OrderPaymentMethod orderPaymentMethod;
    private OrderPaymentStatus orderPaymentStatus;
    String notes;
}
