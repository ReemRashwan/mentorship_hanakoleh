package com.mentorship.hanakoleh.domain.restaurant.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
@Builder
    public record AddRestaurantRequest(
            @NotBlank String name,
            @NotBlank String phone,
            @NotBlank @Email String email,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude,
            @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @Min(0) BigDecimal deliveryRadiusInKm,
            @Min(0) Integer avgPreparationTimeInMins,
            @NotNull Integer restaurantCategoryId
    ) {}
