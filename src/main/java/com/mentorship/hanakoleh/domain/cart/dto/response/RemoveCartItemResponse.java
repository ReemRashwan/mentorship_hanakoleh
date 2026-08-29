package com.mentorship.hanakoleh.domain.cart.dto.response;

import java.math.BigDecimal;

public record RemoveCartItemResponse(
        Integer cartId,
        Long itemId,
        Integer totalItems,
        BigDecimal subtotal,
        Long restaurantId
) {
}
