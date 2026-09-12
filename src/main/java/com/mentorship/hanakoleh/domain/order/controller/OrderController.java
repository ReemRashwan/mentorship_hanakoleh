package com.mentorship.hanakoleh.domain.order.controller;

import com.mentorship.hanakoleh.domain.order.dto.CurrentOrderResponse;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetailsResponse;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;
import com.mentorship.hanakoleh.domain.order.service.OrderService;
import java.util.List;
import com.mentorship.hanakoleh.domain.user.AuthenticationFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

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
