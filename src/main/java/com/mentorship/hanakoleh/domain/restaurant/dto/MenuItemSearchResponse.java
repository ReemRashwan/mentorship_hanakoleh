package com.mentorship.hanakoleh.domain.restaurant.dto;

import java.math.BigDecimal;

public record MenuItemSearchResponse(
        Integer menuItemId,
        String name,
        Integer restaurantId,
        String restaurantName,
        BigDecimal price,
        String category,
        boolean available) {
}
