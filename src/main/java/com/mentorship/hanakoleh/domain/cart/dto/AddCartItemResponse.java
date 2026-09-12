package com.mentorship.hanakoleh.domain.cart.dto;

import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record AddCartItemResponse(
        Integer cartId,
        Integer restaurantId,
        CartStatus status,
        Integer totalItemCount,
        BigDecimal totalPrice,
        List<CartItemDTO> processedCartItems) {
}
