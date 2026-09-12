package com.mentorship.hanakoleh.domain.order.event;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRefundProcessedEvent {
    private Long orderId;
    private OrderFinalStatus orderFinalStatus;
}

