package com.mentorship.hanakoleh.domain.restaurant.dto;

import java.math.BigDecimal;

public record RecommendedRestaurantResponse(
        Integer restaurantId,
        String name,
        String category,
        BigDecimal rating,
        String reason) {
}
