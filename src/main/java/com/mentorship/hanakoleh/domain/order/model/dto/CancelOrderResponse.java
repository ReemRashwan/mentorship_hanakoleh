package com.mentorship.hanakoleh.domain.order.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelOrderResponse {
    private Long orderId;
}
