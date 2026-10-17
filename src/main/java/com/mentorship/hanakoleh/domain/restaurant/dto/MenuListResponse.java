package com.mentorship.hanakoleh.domain.restaurant.dto;

import java.time.OffsetDateTime;

public record MenuListResponse(
        Integer menuId,
        String name,
        String category,
        boolean isActive,
        long itemCount,
        OffsetDateTime updatedAt) {
}
