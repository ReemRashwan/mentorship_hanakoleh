package com.mentorship.hanakoleh.domain.order.model.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderCancellationTrigger;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelOrderRequest {
    Long orderId;
    Integer userId;
    OrderCancellationTrigger cancellationTrigger;
    String reason;
    String notes;
}
