package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.domain.checkout.mapper.OrderResponseMapper;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.service.OrderService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@org.springframework.core.annotation.Order(10)
@RequiredArgsConstructor
public class IdempotencyStepService implements PlaceOrderStep {

    private final OrderService orderService;
    private final OrderResponseMapper orderResponseMapper;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        if (context.getIdempotencyKey() != null) {
            Optional<Order> existing = orderService.findByIdempotencyKey(context.getIdempotencyKey());
            if (existing.isPresent()) {
                Order order = existing.get();
                context.setResponse(orderResponseMapper.toResponse(order, orderService.items(order.getId())));
                return;
            }
        }
        chain.next(context);
    }
}
