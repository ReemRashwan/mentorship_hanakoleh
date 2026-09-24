package com.mentorship.hanakoleh.domain.order.dto;

import lombok.Builder;


@Builder
public record RefundOrderResponse(
        Long orderId
) {
}
