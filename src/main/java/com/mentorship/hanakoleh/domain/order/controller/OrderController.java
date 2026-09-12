package com.mentorship.hanakoleh.domain.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.mentorship.hanakoleh.domain.order.constants.OrderConstants;
import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.service.OrderService;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/orders")
@Tag(name = "Order", description = "Order management API")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/history")
    @Operation(summary = "Get order history for a user for the past 3 months")
    public ResponseEntity<Page<OrderHistoryResponse>> getOrderHistory(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderHistoryResponse> response = orderService.getHistoricalOrders(customerId, pageable);
        return ResponseEntity.ok(response);
    }

}
