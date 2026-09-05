package com.mentorship.hanakoleh.domain.cart.dto;

import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record AddCartItemResponse(
        Integer restaurantId,
        CartStatus status,
        List<CartItemDTO> returnedCartItems) {
}
