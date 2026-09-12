package com.mentorship.hanakoleh.domain.order.controller;

import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.dto.CurrentOrderResponse;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetailsResponse;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.dto.UpdateOrderStatusRequest;
import com.mentorship.hanakoleh.domain.order.service.OrderService;
import java.util.List;
import com.mentorship.hanakoleh.domain.user.AuthenticationFunction;
import com.mentorship.hanakoleh.domain.order.service.OrderStatusUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final OrderStatusUpdateService orderStatusUpdateService;
    private final OrderMapper orderMapper;

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderFinalStatus> updateOrderStatus(
            @PathVariable("orderId") String orderId,
            @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
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
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authorizationHeader) {
        Integer customerId = AuthenticationFunction.extractID(authorizationHeader);
        OrderDetailsResponse response = orderMapper.toOrderDetailsResponse(
                orderService.getOrder(orderId, customerId));
        return ResponseEntity.ok(response);
    }
}
