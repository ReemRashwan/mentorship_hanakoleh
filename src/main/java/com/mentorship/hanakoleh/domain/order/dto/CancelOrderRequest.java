package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderCancellationTrigger;
import lombok.Builder;


@Builder
public record CancelOrderRequest (
    Long orderId,
    Integer restaurantId,
    OrderCancellationTrigger cancellationTrigger,
    String reason,
    String notes
)
    {

}
