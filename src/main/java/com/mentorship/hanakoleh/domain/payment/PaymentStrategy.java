package com.mentorship.hanakoleh.domain.payment;

import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import java.util.Set;

public interface PaymentStrategy {
    Set<OrderPaymentMethod> supportedMethods();
    PaymentInitiationResult initiate(Order order);
}
