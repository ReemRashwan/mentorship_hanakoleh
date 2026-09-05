package com.mentorship.hanakoleh.domain.cart.controller;

import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityRequest;
import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.dto.response.RemoveCartItemResponse;
import com.mentorship.hanakoleh.domain.cart.mapper.CartMapper;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Cart", description = "Cart management API")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @PatchMapping("/cart/items/{cartItemId}")
    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of an item in the cart")
    public UpdateCartItemQuantityResponse updateItemQuantity(
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        return cartService.updateItemQuantity(cartItemId, request.getQuantity());
    }

    @DeleteMapping("/v1/carts/{cartId}/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes an item from the cart. If it's the last item, the cart status changes to EMPTY and restaurant is cleared")
    public ResponseEntity<RemoveCartItemResponse> removeCartItem(
            @PathVariable Integer cartId,
            @PathVariable Integer itemId) {
        Cart cart = cartService.removeCartItem(cartId, itemId);
        RemoveCartItemResponse response = cartMapper.toResponse(cart, itemId.longValue());
        return ResponseEntity.ok(response);
    }
}
