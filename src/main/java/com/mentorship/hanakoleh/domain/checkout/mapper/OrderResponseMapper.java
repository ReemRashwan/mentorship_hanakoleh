package com.mentorship.hanakoleh.domain.checkout.mapper;

import com.mentorship.hanakoleh.domain.checkout.dto.OrderResponse;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMapper {

    public OrderResponse toResponse(Order order, List<OrderItem> lines) {
        List<OrderResponse.OrderLine> items = lines.stream()
                .map(line -> new OrderResponse.OrderLine(
                        line.getMenuItem().getId(), line.getNameSnapshot(), line.getQuantity(),
                        line.getPrice(), line.getSubtotal()))
                .toList();
        return new OrderResponse(
                order.getId(), order.getIdempotencyKey().toString(), order.getDeliveryOption(),
                order.getFinalStatus(), order.getPaymentStatus(), order.getPaymentMethod(), order.getCurrencyCode(),
                order.getSubtotal(), order.getDeliveryFees(), order.getServiceFees(), order.getRiderTips(),
                order.getDiscountAmount(), order.getTaxAmount(), order.getTotalAmount(),
                order.getEstimatedDeliveryAt(), items);
    }
}
