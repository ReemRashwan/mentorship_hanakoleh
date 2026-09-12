package com.mentorship.hanakoleh.domain.order.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class OrderStatusUpdateEvent extends ApplicationEvent {
    public OrderStatusUpdateEvent(Object source) {
        super(source);
    }
}
