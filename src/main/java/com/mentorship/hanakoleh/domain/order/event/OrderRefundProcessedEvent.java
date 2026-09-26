package com.mentorship.hanakoleh.domain.order.event;


import com.mentorship.hanakoleh.domain.order.model.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OrderRefundProcessedEvent extends OrderEvent {
    public static OrderRefundProcessedEvent fromOrder(Order activeOrder) {
        return OrderRefundProcessedEvent.builder().orderId(activeOrder.getId()).build();
    }
}

