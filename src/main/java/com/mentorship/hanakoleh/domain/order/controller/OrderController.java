package com.mentorship.hanakoleh.domain.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mentorship.hanakoleh.domain.order.constants.OrderConstants;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/orders")
@Tag(name= "Order", description= "Order managementAPI")
public class OrderController {

    @PathVariable Integer customerId;

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController (OrderService orderService, OrderMapper orderMapper){
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }
    @GetMapping("/history")
    @Operation(summary = "Get order history for a user for the past 3 months")
    public ResponseEntity<OrderHistoryResponse> getOrderHistory(@pathVariable Integer customerId){
        orderService.getHistoricalOrdersForThePastNumberOfMonths(OrderConstants.NUMBER_OF_MONTHS_FOR_HISTORICAL_ORDERS, customerId);
        return ResponseEntity.ok(response);
    }
    

}
