package com.mentorship.hanakoleh.domain.order.event;



import com.mentorship.hanakoleh.domain.order.dto.UpdateOrderStatusRequest;
import com.mentorship.hanakoleh.domain.order.model.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OrderDeliveredEvent extends OrderEvent {
    public static OrderDeliveredEvent fromOrder(Order activeOrder, UpdateOrderStatusRequest updateStatusRequest, Integer actorUserId) {
        return OrderDeliveredEvent.builder()
                .orderId(activeOrder.getId())
                .finalStatus(updateStatusRequest.nextOrderStatus())
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId).build();
    }
}
