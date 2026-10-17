package com.mentorship.hanakoleh.domain.restaurant.dto;

import java.time.OffsetDateTime;

public record MenuResponse(
        Integer id,
        Integer restaurantId,
        String name,
        String icon,
        Integer uiOrder,
        Boolean visible
) {
}

