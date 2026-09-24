package com.mentorship.hanakoleh.domain.order.controller;

import com.mentorship.hanakoleh.domain.order.dto.*;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;
import com.mentorship.hanakoleh.domain.order.service.OrderService;
import java.util.List;
import com.mentorship.hanakoleh.domain.user.AuthenticationFunction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "Order management API")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<UpdateOrderStatusResponse> updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        Integer actorUserId = AuthenticationFunction.extractID(authorizationHeader);
        UpdateOrderStatusResponse response = orderService.updateOrderStatus(orderId,actorUserId, updateOrderStatusRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "{orderId}/cancel")
    @Operation(summary = "Cancel Order")
    ResponseEntity<CancelOrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid CancelOrderRequest request
    ) {
        Integer actorUserId = AuthenticationFunction.extractID(authorizationHeader);
        CancelOrderResponse response = orderService.cancelOrder(orderId, actorUserId,request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "{orderId}/refund")
    @Operation(summary = "Refund Order")
    ResponseEntity<RefundOrderResponse> refundOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid RefundOrderRequest request
    ) {
        Integer actorUserId = AuthenticationFunction.extractID(authorizationHeader);
        RefundOrderResponse response = orderService.refundOrder(orderId, actorUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get order history for a user for the past 3 months")
    public ResponseEntity<Page<OrderHistoryResponse>> getOrderHistory(
            @RequestParam Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderHistoryResponse> response = orderService.getHistoricalOrders(customerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<List<CurrentOrderResponse>> getCurrentOrders(@RequestHeader("Authorization") String authorizationHeader) {
        Integer customerId = AuthenticationFunction.extractID(authorizationHeader);
        List<CurrentOrderResponse> orders = orderService.getCurrentOrders(customerId).stream()
                .map(orderMapper::toCurrentOrderResponse)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsResponse> getOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        Integer customerId = AuthenticationFunction.extractID(authorizationHeader);
        OrderDetailsResponse response = orderMapper.toOrderDetailsResponse(
                orderService.getOrder(id, customerId));
        return ResponseEntity.ok(response);
    }
}
