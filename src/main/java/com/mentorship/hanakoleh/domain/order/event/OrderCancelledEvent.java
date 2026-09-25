package com.mentorship.hanakoleh.domain.order.event;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OrderCancelledEvent extends OrderEvent {
    String reason;
}
