package com.mentorship.hanakoleh.domain.order.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderMarkedReadyForDeliveryEvent {
    private Long orderId;
}
