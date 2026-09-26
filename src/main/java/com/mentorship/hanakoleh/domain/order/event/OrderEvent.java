package com.mentorship.hanakoleh.domain.order.event;


import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public abstract class OrderEvent {
    final Long orderId;
    final OrderFinalStatus finalStatus;
    final String eventTrigger;
    final Integer actorUserId;

}
