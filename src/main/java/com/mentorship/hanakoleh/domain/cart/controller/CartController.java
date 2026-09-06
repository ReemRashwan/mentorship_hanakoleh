package com.mentorship.hanakoleh.domain.cart.controller;

import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemRequest;
import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemResponse;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/v1/carts/items")
    @Operation(summary = "Add Item to cart", description = "Add a new menu item to the cart, or increase the quantity of an existing item.")
    public ResponseEntity<AddCartItemResponse> requestAddMenuItemToCart(@Valid @RequestBody AddCartItemRequest addCartItemRequestDto,
                                                                        Integer userId) {// throws InterruptedException {
        AddCartItemResponse finalCartState =
                cartService.addItemToCart(addCartItemRequestDto, userId);
        return new ResponseEntity<>(finalCartState,
                HttpStatus.CREATED);

    }
}
