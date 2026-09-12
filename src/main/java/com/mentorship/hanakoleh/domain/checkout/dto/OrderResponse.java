package com.mentorship.hanakoleh.domain.checkout.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String idempotencyKey,
        OrderDeliveryOption deliveryOption,
        OrderFinalStatus finalStatus,
        OrderPaymentStatus paymentStatus,
        OrderPaymentMethod paymentMethod,
        String currencyCode,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal serviceFee,
        BigDecimal riderTip,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        OffsetDateTime estimatedDeliveryAt,
        List<OrderLine> items) {

    public record OrderLine(
            Integer menuItemId,
            String name,
            int quantity,
            BigDecimal price,
            BigDecimal subtotal) {
    }
}