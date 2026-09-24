package com.mentorship.hanakoleh.domain.order.event;



import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class OrderDeliveredEvent extends OrderEvent {
}
