package com.mentorship.hanakoleh.domain.payment;

import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.payment.exception.PaymentMethodNotSupportedException;
import com.mentorship.hanakoleh.exception.ErrorCode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PaymentStrategyResolver {

    private final Map<OrderPaymentMethod, PaymentStrategy> strategiesByMethod;

    public PaymentStrategyResolver(List<PaymentStrategy> strategies) {
        Map<OrderPaymentMethod, PaymentStrategy> map = new EnumMap<>(OrderPaymentMethod.class);
        for (PaymentStrategy strategy : strategies) {
            for (OrderPaymentMethod method : strategy.supportedMethods()) {
                map.put(method, strategy);
            }
        }
        this.strategiesByMethod = map;
    }

    public PaymentStrategy resolve(OrderPaymentMethod method) {
        PaymentStrategy strategy = strategiesByMethod.get(method);
        if (strategy == null) {
            throw new PaymentMethodNotSupportedException(
                    ErrorCode.PAYMENT_METHOD_NOT_SUPPORTED.format(method));
        }
        return strategy;
    }
}
