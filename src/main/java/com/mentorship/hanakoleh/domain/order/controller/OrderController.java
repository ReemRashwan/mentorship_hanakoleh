package com.mentorship.hanakoleh.domain.order.controller;

import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.dto.UpdateOrderStatusRequest;
import com.mentorship.hanakoleh.domain.order.service.OrderStatusUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    private OrderStatusUpdateService orderService;
    public OrderController(OrderStatusUpdateService orderService) {
        this.orderService = orderService;
    }
    @PatchMapping("v1/orders/{orderId}")
    ResponseEntity<OrderFinalStatus> updateOrderStatus(@PathVariable("orderId") String orderId, @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
