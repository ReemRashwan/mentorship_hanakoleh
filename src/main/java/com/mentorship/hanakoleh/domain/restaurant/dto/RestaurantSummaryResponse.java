package com.mentorship.hanakoleh.domain.restaurant.dto;

import java.math.BigDecimal;

public record RestaurantSummaryResponse(
        Integer restaurantId,
        String name,
        String category,
        BigDecimal rating,
        Integer reviewCount) {
}
