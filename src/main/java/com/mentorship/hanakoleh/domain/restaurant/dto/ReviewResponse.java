package com.mentorship.hanakoleh.domain.restaurant.dto;

import java.time.OffsetDateTime;

public record ReviewResponse(
        Integer id,
        Integer restaurantId,
        Integer customerId,
        Integer rating,
        String comment,
        OffsetDateTime createdAt
) {
}

