package com.mentorship.hanakoleh.domain.cart.dto;

import java.math.BigDecimal;

public record UpdateCartItemQuantityResponse(
        Integer cartItemId,
        Integer menuItemId,
        Integer quantity,
        BigDecimal price,
        String note) {

}
