package com.mentorship.hanakoleh.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddCartItemRequest(@Positive
                                 Integer selectedMenuItemId,
                                 @Positive
                                 Integer restaurantId,
                                 @Min(1)
                                 Integer quantity,
                                 String note) {
}
