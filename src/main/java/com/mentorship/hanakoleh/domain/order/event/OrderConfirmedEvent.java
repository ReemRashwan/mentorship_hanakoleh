package com.mentorship.hanakoleh.domain.order.event;

import com.mentorship.hanakoleh.domain.order.dto.UpdateOrderStatusRequest;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
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

    public static OrderConfirmedEvent fromOrder(Order activeOrder, UpdateOrderStatusRequest updateStatusRequest, Integer actorUserId) {
        return OrderConfirmedEvent.builder().orderId(activeOrder.getId())
                .orderPaymentMethod(activeOrder.getPaymentMethod())
                .orderPaymentStatus(activeOrder.getPaymentStatus())
                .notes(updateStatusRequest.notes())
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId)
                .finalStatus(OrderFinalStatus.CONFIRMED).build();
    }
}
