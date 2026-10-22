package com.mentorship.hanakoleh.domain.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

//        Restaurant.builder().id().name().phone().email().rating().longitude().latitude().avgPreparationTimeInMins().createdAt().deliveryRadiusKm().reviewCount().category().build()
@Builder
public record UpdateRestaurantRequest(

        @Pattern(regexp = ".*\\S.*", message = "name must not be blank")
        @Size(max = 100)
        String name,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phone must be 7-15 digits, optionally starting with +")
        String phone,

        @Email
        @Size(max = 254)
        String email,

        @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
        @Digits(integer = 3, fraction = 7)
        BigDecimal longitude,

        @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
        @Digits(integer = 2, fraction = 7)
        BigDecimal latitude,

        @Positive @Max(120)
        Integer avgPreparationTimeInMins,

        @DecimalMin(value = "0.0", inclusive = false) @DecimalMax("150.0")
        @Digits(integer = 3, fraction = 2)
        BigDecimal deliveryRadiusKm,

        @Positive
        Integer categoryId
) {

    @JsonIgnore
    @AssertTrue(message = "latitude and longitude must be provided together")
    public boolean isCoordinatesPairComplete() {
        return ((latitude == null) && (longitude == null));
    }
}
