package com.mentorship.hanakoleh.domain.checkout.controller;

import com.mentorship.hanakoleh.domain.checkout.dto.OrderResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.OrderTotalsResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.PlaceOrderRequest;
import com.mentorship.hanakoleh.domain.checkout.service.CheckoutService;
import com.mentorship.hanakoleh.domain.checkout.service.PlaceOrderService;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkout")
@Tag(name = "Checkout", description = "Checkout: review the bill, then place the order")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final PlaceOrderService placeOrderService;

    public CheckoutController(CheckoutService checkoutService, PlaceOrderService placeOrderService) {
        this.checkoutService = checkoutService;
        this.placeOrderService = placeOrderService;
    }

    @GetMapping("/review")
    @Operation(summary = "Review checkout",
            description = "Validates the cart and composes subtotal, delivery fee, tip, tax and promotion "
                    + "discount into the final total and ETA. Read-only - places nothing.")
    public ResponseEntity<OrderTotalsResponse> review(
            @RequestHeader("X-Customer-Id") Integer customerId,
            @RequestParam("option") OrderDeliveryOption option,
            @RequestParam(value = "addressId", required = false) Long addressId,
            @RequestParam(value = "promoCode", required = false) String promoCode,
            @RequestParam(value = "riderTip", required = false) BigDecimal riderTip) {
        return ResponseEntity.ok(
                checkoutService.computeTotals(customerId, option, addressId, promoCode, riderTip));
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place order",
            description = "Runs the place-order cycle: validates the cart, reprices, resolves delivery, applies "
                    + "the promo, computes totals, prepares (does not charge) payment, persists the order and its "
                    + "lines, records tracking and converts the cart - all atomically. Pass an Idempotency-Key "
                    + "header to make retries safe.")
    public OrderResponse placeOrder(
            @RequestHeader("X-Customer-Id") Integer customerId,
            @RequestHeader(value = "Idempotency-Key", required = false) UUID idempotencyKey,
            @Valid @RequestBody PlaceOrderRequest request) {
        return placeOrderService.placeOrder(customerId, idempotencyKey, request);
    }
}
