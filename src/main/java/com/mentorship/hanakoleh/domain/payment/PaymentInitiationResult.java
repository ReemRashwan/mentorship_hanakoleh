package com.mentorship.hanakoleh.domain.payment;

import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;

public record PaymentInitiationResult(
        OrderPaymentStatus status,
        String gatewayReference,
        boolean requiresClientAction) {
}
