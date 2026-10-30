package com.mentorship.hanakoleh.domain.restaurant.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RestaurantSearchCriteria(
        @Size(max = 150) String name,
        @Positive Integer maxAvgPreparationTimeInMins,
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal rating,
        @Positive BigDecimal minDeliveryRadiusKm,
        @Positive Integer categoryId
) {}
