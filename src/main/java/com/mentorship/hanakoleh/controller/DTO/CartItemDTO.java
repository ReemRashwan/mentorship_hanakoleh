package com.mentorship.hanakoleh.controller.DTO;

import com.mentorship.hanakoleh.domain.cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder

public class CartItemDTO {
    private Integer selectedMenuItemId;
    private BigDecimal price;
    private Integer quantity;
    private String  note;

    public CartItemDTO( ) {

    }
}
