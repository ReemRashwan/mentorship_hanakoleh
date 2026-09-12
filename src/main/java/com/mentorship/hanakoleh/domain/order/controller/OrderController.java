package com.mentorship.hanakoleh.domain.order.controller;

import com.mentorship.hanakoleh.domain.order.dto.PlaceOrderRequest;
import com.mentorship.hanakoleh.domain.order.dto.PlaceOrderResponse;
import com.mentorship.hanakoleh.domain.order.service.MockOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final MockOrderService orderService;

    @PostMapping(path = "/place/{customerId}")
    ResponseEntity<PlaceOrderResponse> placeOrder(
            @PathVariable Integer customerId,
            @RequestBody @Valid PlaceOrderRequest request
    ) {
        PlaceOrderResponse response = orderService.placeOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}