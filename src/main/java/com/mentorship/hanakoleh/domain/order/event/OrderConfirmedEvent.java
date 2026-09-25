package com.mentorship.hanakoleh.domain.order.event;

import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder

public class OrderConfirmedEvent extends OrderEvent   {
    private OrderPaymentMethod orderPaymentMethod;
    private OrderPaymentStatus orderPaymentStatus;
    String notes;
}
