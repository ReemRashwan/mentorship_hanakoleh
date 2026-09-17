package com.mentorship.hanakoleh.domain.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelOrderResponse {
    private Long orderId;
}
