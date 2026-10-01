package com.mentorship.hanakoleh.domain.checkout.service;

import com.mentorship.hanakoleh.domain.checkout.dto.OrderResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.PlaceOrderRequest;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaceOrderService {

    private final List<PlaceOrderStep> steps;

    public PlaceOrderService(List<PlaceOrderStep> steps) {
        this.steps = steps;
    }

    @Transactional
    public OrderResponse placeOrder(Integer customerId, UUID idempotencyKey, PlaceOrderRequest request) {
        PlaceOrderContext context = new PlaceOrderContext();
        context.setCustomerId(customerId);
        context.setIdempotencyKey(idempotencyKey);
        context.setRequest(request);

        new PlaceOrderChain(steps).next(context);

        return context.getResponse();
    }
}
