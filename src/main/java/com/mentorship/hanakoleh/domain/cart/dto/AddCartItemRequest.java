package com.mentorship.hanakoleh.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.math.BigDecimal;


@Builder
public record AddCartItemRequest(@NotEmpty
                                 Integer selectedMenuItemId,
                                 @NotEmpty
                                 Integer restaurantId,
                                 @NotEmpty
                                 BigDecimal price,
                                 @Min(1)
                                 Integer quantity,
                                 String note) {
}
