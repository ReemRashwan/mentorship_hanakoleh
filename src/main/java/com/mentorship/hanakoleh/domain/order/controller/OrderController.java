package com.mentorship.hanakoleh.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.dto.UpdateOrderStatusRequest;
import com.mentorship.hanakoleh.domain.order.service.OrderStatusUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusUpdateService orderStatusUpdateService;
    private final OrderMapper orderMapper;


    @PatchMapping("v1/orders/{orderId}")
    ResponseEntity<OrderFinalStatus> updateOrderStatus(@PathVariable("orderId") String orderId, @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

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
