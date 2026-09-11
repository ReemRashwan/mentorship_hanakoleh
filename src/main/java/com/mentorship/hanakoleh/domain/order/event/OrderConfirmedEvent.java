package com.mentorship.hanakoleh.domain.order.event;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderConfirmedEvent   {
    private Long orderId;
    private OrderPaymentMethod orderPaymentMethod;
    private OrderPaymentStatus orderPaymentStatus;
    private OrderFinalStatus orderFinalStatus;
}
