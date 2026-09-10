package com.mentorship.hanakoleh.domain.checkout.controller;

import com.mentorship.hanakoleh.domain.cart.dto.CartPricingResponse;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.checkout.dto.CartValidationResponse;
import com.mentorship.hanakoleh.domain.checkout.mapper.CheckoutCartMapper;
import com.mentorship.hanakoleh.domain.checkout.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@Tag(name = "Checkout", description = "Checkout flow API")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final CheckoutCartMapper checkoutCartMapper;

    public CheckoutController(CheckoutService checkoutService, CheckoutCartMapper checkoutCartMapper) {
        this.checkoutService = checkoutService;
        this.checkoutCartMapper = checkoutCartMapper;
    }

    @GetMapping("/cart-validation")
    @Operation(summary = "Validate cart for checkout",
            description = "Loads the customer's cart and validates it is active, non-empty and orderable.")
    public ResponseEntity<CartValidationResponse> validateCart(
            @RequestHeader("X-Customer-Id") Integer customerId) {
        Cart cart = checkoutService.loadAndValidateCart(customerId);
        return ResponseEntity.ok(checkoutCartMapper.toValidationResponse(cart));
    }

    @GetMapping("/cart-pricing")
    @Operation(summary = "Re-price cart for checkout",
            description = "Validates the cart, recomputes each line from live menu prices, and returns the subtotal.")
    public ResponseEntity<CartPricingResponse> repriceCart(
            @RequestHeader("X-Customer-Id") Integer customerId) {
        var repriceCartResponse = checkoutService.repriceCart(customerId);
        return ResponseEntity.ok(repriceCartResponse);
    }
}
