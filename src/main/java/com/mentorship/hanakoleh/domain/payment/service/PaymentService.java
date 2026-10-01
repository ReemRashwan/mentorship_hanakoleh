package com.mentorship.hanakoleh.domain.payment.service;

import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.payment.PaymentInitiationResult;
import com.mentorship.hanakoleh.domain.payment.PaymentStrategy;
import com.mentorship.hanakoleh.domain.payment.PaymentStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentStrategyResolver paymentStrategyResolver;

    public PaymentInitiationResult prepare(Order order) {
        PaymentStrategy strategy = paymentStrategyResolver.resolve(order.getPaymentMethod());
        PaymentInitiationResult result = strategy.initiate(order);
        order.setPaymentStatus(result.status());
        return result;
    }
}
