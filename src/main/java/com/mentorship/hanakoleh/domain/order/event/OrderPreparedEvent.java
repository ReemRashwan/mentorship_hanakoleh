package com.mentorship.hanakoleh.domain.order.event;

import com.mentorship.hanakoleh.domain.order.dto.UpdateOrderStatusRequest;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OrderPreparedEvent extends OrderEvent {
    public static OrderPreparedEvent fromOrder(Order activeOrder, UpdateOrderStatusRequest updateStatusRequest, Integer actorUserId) {
        return OrderPreparedEvent.builder().orderId(activeOrder.getId())
                .finalStatus(OrderFinalStatus.READY_FOR_PICKUP)
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId).build();
    }
}
