package com.mentorship.hanakoleh.domain.cart.dto;

import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemDTO {
    private final Integer menuItemId;
    private final Integer quantity;
    private final String note;
    private final BigDecimal subTotal;

}
