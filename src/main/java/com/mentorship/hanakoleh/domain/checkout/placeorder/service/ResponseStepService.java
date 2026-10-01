package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.domain.checkout.mapper.OrderResponseMapper;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(80)
@RequiredArgsConstructor
public class ResponseStepService implements PlaceOrderStep {

    private final OrderResponseMapper orderResponseMapper;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        context.setResponse(orderResponseMapper.toResponse(context.getOrder(), context.getItems()));
        chain.next(context);
    }
}
