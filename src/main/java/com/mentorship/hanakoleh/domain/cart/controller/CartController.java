package com.mentorship.hanakoleh.domain.cart.controller;

import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityRequest;
import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PatchMapping("/items/{cartItemId}")
    public UpdateCartItemQuantityResponse updateItemQuantity(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        return cartService.updateItemQuantity(cartItemId, request.getQuantity());
    }
}
