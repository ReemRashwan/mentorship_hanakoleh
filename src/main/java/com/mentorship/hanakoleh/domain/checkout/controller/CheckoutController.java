package com.mentorship.hanakoleh.domain.checkout.controller;

import com.mentorship.hanakoleh.domain.checkout.dto.CartPricingResponse;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.checkout.dto.CartValidationResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.DeliveryAddressResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.DeliveryOptionResponse;
import com.mentorship.hanakoleh.domain.checkout.mapper.CheckoutCartMapper;
import com.mentorship.hanakoleh.domain.checkout.service.CheckoutService;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/delivery-address")
    @Operation(summary = "Resolve & validate delivery address",
            description = "Resolves the delivery address (given id, or the customer's default) and validates ownership and location.")
    public ResponseEntity<DeliveryAddressResponse> resolveDeliveryAddress(
            @RequestHeader("X-Customer-Id") Integer customerId,
            @RequestParam(value = "addressId", required = false) Long addressId) {
        var deliveryAddressResponse = checkoutService.resolveDeliveryAddress(customerId, addressId);
        return ResponseEntity.ok(deliveryAddressResponse);
    }

    @GetMapping("/delivery-option")
    @Operation(summary = "Resolve delivery option",
            description = "Resolves the chosen option's fee and ETA; for DELIVERY, validates the address is within the restaurant's zone.")
    public ResponseEntity<DeliveryOptionResponse> resolveDeliveryOption(
            @RequestHeader("X-Customer-Id") Integer customerId,
            @RequestParam("option") OrderDeliveryOption option,
            @RequestParam(value = "addressId", required = false) Long addressId) {
        var deliveryOptionResponse = checkoutService.resolveDeliveryOption(customerId, option, addressId);
        return ResponseEntity.ok(deliveryOptionResponse);
    }
}
