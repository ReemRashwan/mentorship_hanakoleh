package com.mentorship.hanakoleh.domain.restaurant.dto;


import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantCategory;

import java.math.BigDecimal;

public record RestaurantDTO (Integer restaurantId, String name, String phone, String email,  BigDecimal rating, Integer avgPreparationTimeInMins, BigDecimal deliveryRadiusKm, Integer reviewCount, RestaurantCategory category) {
}
