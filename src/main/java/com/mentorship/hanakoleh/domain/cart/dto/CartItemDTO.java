package com.mentorship.hanakoleh.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CartItemDTO {
    private Integer selectedMenuItemId;
    private BigDecimal price;
    private Integer quantity;
    private String note;

}
