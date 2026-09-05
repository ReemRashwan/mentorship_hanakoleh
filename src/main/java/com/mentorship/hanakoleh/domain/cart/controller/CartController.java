package com.mentorship.hanakoleh.domain.cart.controller;

import com.mentorship.hanakoleh.domain.cart.dto.ClearCartResponse;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {
    CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @DeleteMapping("/v1/carts/items")
    @Operation(summary = "Clear Cart", description = "To delete all items currently existing in the csutomer cart.")

    public ResponseEntity<ClearCartResponse> clearCart(Integer userId) {

        ClearCartResponse clearedCart = cartService.clearCart(userId);
        return ResponseEntity.ok(clearedCart);
    }

}
