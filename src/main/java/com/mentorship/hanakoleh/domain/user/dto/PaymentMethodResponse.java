package com.mentorship.hanakoleh.domain.user.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PaymentMethodResponse(
        Integer id,
        String brand,
        String last4,
        Integer expiryMonth,
        Integer expiryYear,
        Boolean isDefault,
        Instant createdAt
) {
}
