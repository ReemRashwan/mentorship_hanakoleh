package com.mentorship.hanakoleh.domain.checkout.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderTotalsResponse(
        OrderDeliveryOption deliveryOption,
        String currencyCode,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal serviceFee,
        BigDecimal riderTip,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        int estimatedMinutes,
        OffsetDateTime estimatedDeliveryAt) {
}