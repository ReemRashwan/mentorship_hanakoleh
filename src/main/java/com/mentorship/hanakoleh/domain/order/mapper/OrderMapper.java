package com.mentorship.hanakoleh.domain.order.mapper;

import com.mentorship.hanakoleh.domain.order.dto.CurrentOrderResponse;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetails;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetailsResponse;
import com.mentorship.hanakoleh.domain.order.dto.OrderItemResponse;

import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public CurrentOrderResponse toCurrentOrderResponse(Order order) {
        return new CurrentOrderResponse(
                order.getId(),
                order.getRestaurant().getName(),
                order.getFinalStatus(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getCurrencyCode());
    }

    public OrderDetailsResponse toOrderDetailsResponse(OrderDetails orderDetails) {
        Order order = orderDetails.order();
        return new OrderDetailsResponse(
                order.getId(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getFinalStatus(),
                order.getDeliveryOption(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getCurrencyCode(),
                order.getSubtotal(),
                order.getDeliveryFees(),
                order.getServiceFees(),
                order.getRiderTips(),
                order.getDiscountAmount(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getRefundedAmount(),
                order.getDeliveryInstructions(),
                order.getDeliveryAddressSnapshot(),
                order.getEstimatedDeliveryAt(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                orderDetails.items().stream().map(this::toOrderItemResponse).toList());
    }

    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getMenuItem().getId(),
                orderItem.getNameSnapshot(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getSubtotal(),
                orderItem.getOptionsSnapshot(),
                orderItem.getSpecialInstructions());
    }

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.restaurant.name", target = "restaurantName")
    @Mapping(source = "order.finalStatus", target = "status")
    @Mapping(source = "order.totalAmount", target = "totalAmount")
    @Mapping(source = "order.currencyCode", target = "currencyCode")
    @Mapping(source = "itemLineCount", target = "itemLineCount")
    @Mapping(source = "order.createdAt", target = "createdAt")
    OrderHistoryResponse toOrderHistoryResponse(Order order, long itemLineCount);
}
