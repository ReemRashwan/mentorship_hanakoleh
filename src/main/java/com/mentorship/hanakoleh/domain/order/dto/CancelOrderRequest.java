package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderCancellationTrigger;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record CancelOrderRequest (
    Long orderId,
    Integer restaurantId,
    OrderCancellationTrigger cancellationTrigger,
    @NotBlank
    String reason,
    String notes
)
    {

}
