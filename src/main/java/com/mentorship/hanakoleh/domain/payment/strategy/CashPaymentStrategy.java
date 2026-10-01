package com.mentorship.hanakoleh.domain.payment.strategy;

import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import com.mentorship.hanakoleh.domain.payment.PaymentInitiationResult;
import com.mentorship.hanakoleh.domain.payment.PaymentStrategy;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public Set<OrderPaymentMethod> supportedMethods() {
        return Set.of(OrderPaymentMethod.CASH);
    }

    @Override
    public PaymentInitiationResult initiate(Order order) {
        return new PaymentInitiationResult(OrderPaymentStatus.PENDING, null, false);
    }
}
