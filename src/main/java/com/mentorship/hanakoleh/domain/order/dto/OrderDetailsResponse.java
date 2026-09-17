package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record OrderDetailsResponse(
        Long orderId,
        Integer restaurantId,
        String restaurantName,
        OrderFinalStatus status,
        OrderDeliveryOption deliveryOption,
        OrderPaymentStatus paymentStatus,
        OrderPaymentMethod paymentMethod,
        String currencyCode,
        BigDecimal subtotal,
        BigDecimal deliveryFees,
        BigDecimal serviceFees,
        BigDecimal riderTips,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        BigDecimal refundedAmount,
        String deliveryInstructions,
        Map<String, Object> deliveryAddress,
        OffsetDateTime estimatedDeliveryAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<OrderItemResponse> items
) {
}
