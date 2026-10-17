package com.mentorship.hanakoleh.domain.restaurant.dto;

import java.math.BigDecimal;

public record MenuItemSearchCriteria(
        String query,
        Integer restaurantId,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        boolean availableOnly) {
}
