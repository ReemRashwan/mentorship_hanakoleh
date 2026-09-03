package com.mentorship.hanakoleh.domain.cart.controller;

import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityRequest;
import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.mapper.CartMapper;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<UpdateCartItemQuantityResponse> updateItemQuantity(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        CartItem cartItem = cartService.updateItemQuantity(cartItemId, request.getQuantity());
        UpdateCartItemQuantityResponse response = cartMapper.toUpdateQuantityResponse(cartItem);
        return ResponseEntity.ok(response);
    }
}
