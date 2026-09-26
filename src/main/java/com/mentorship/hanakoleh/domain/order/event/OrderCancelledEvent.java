package com.mentorship.hanakoleh.domain.order.event;


import com.mentorship.hanakoleh.domain.order.dto.CancelOrderRequest;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OrderCancelledEvent extends OrderEvent {
    String reason;

    public static OrderCancelledEvent fromOrder(Order activeOrder, CancelOrderRequest cancelRequest, Integer actorUserId) {
        return OrderCancelledEvent.builder().orderId(activeOrder.getId())
                .finalStatus(OrderFinalStatus.CANCELLED)
                .eventTrigger(cancelRequest.cancellationTrigger().name())
                .reason(cancelRequest.reason())
                .actorUserId(actorUserId).build();
    }
}
