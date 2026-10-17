package com.mentorship.hanakoleh.domain.payment.strategy;

import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import com.mentorship.hanakoleh.domain.payment.PaymentInitiationResult;
import com.mentorship.hanakoleh.domain.payment.PaymentStrategy;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public Set<OrderPaymentMethod> supportedMethods() {
        return Set.of(OrderPaymentMethod.CREDIT_CARD, OrderPaymentMethod.DEBIT_CARD);
    }

    @Override
    public PaymentInitiationResult initiate(Order order) {
        // TODO(payment): call gateway.createIntent(order) and return AUTHORIZED + client secret.
        return new PaymentInitiationResult(OrderPaymentStatus.PENDING, null, true);
    }
}
