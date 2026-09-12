package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.constants.OrderConstants;
import com.mentorship.hanakoleh.domain.order.model.dto.CancelOrderResponse;
import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.exception.InvalidOrderTransitionException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotOwnedByCustomerException;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetails;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderCancellationTrigger;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.repository.OrderItemRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.projection.OrderItemLineCountProjection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final List<OrderFinalStatus> NON_CURRENT_STATUSES = List.of(OrderFinalStatus.COMPLETED, OrderFinalStatus.CANCELLED, OrderFinalStatus.REFUNDED);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderStatusUpdateService orderStatusUpdateService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderMapper orderMapper, OrderStatusUpdateService orderStatusUpdateService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderStatusUpdateService = orderStatusUpdateService;
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryResponse> getHistoricalOrders(Integer customerId, Pageable pageable) {
        OffsetDateTime startDate = getHistoricalOrderStartDate();
        Page<Order> orders = orderRepository.findByCustomerIdAndCreatedAtGreaterThanEqual(customerId, startDate, pageable);
        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> orderIds = orders.getContent().stream().map(Order::getId).toList();

        List<OrderItemLineCountProjection> lineCounts = orderItemRepository.findLineCountByOrderIds(orderIds);

        Map<Long, Long> lineCountByOrderId = lineCounts.stream().collect(Collectors.toMap(OrderItemLineCountProjection::getOrderId, OrderItemLineCountProjection::getLineCount));

        return orders.map(order -> {
            long lineCount = lineCountByOrderId.getOrDefault(order.getId(), 0L);

            return orderMapper.toOrderHistoryResponse(order, lineCount);
        });

    }

    // Helper methods
    // Helper method to get the start date for the historical orders
    private OffsetDateTime getHistoricalOrderStartDate() {
        return OffsetDateTime.now().minusMonths(OrderConstants.NUMBER_OF_MONTHS_FOR_HISTORICAL_ORDERS);
    }

    @Transactional(readOnly = true)
    public List<Order> getCurrentOrders(Integer customerId) {
        return orderRepository.findByCustomer_IdAndFinalStatusNotInOrderByCreatedAtDesc(customerId, NON_CURRENT_STATUSES);
    }

    @Transactional(readOnly = true)
    public OrderDetails getOrder(Long orderId, Integer customerId) {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, customerId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDetails(order, orderItemRepository.findByOrderIdOrderByIdAsc(orderId));
    }

    @Transactional
    public CancelOrderResponse cancelOrder(Integer cancelingActorUserId, Long orderId, OrderCancellationTrigger cancellationTrigger, String reason, String notes) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %d not found", orderId)));
        switch (cancellationTrigger) {
            case CUSTOMER_CANCELLED -> {
                if (!order.getCustomer().getUser().getId().equals(cancelingActorUserId)) {
                    throw new OrderNotOwnedByCustomerException(orderId, cancelingActorUserId);
                }
                if (order.getFinalStatus() != OrderFinalStatus.CREATED && order.getFinalStatus() != OrderFinalStatus.CONFIRMED) {
                    throw new InvalidOrderTransitionException(order.getFinalStatus(), OrderFinalStatus.CANCELLED);
                }

            }
            case RESTAURANT_CANCELLED -> {
                if (reason == null || reason.isBlank()) {
                    throw new IllegalArgumentException("Reason is required for restaurant emergency cancellation");
                }
            }
            case SLA_BREACH -> {
                // verify breach against Order's stored timestamps needed
            }
        }
        orderStatusUpdateService.cancelOrder(cancelingActorUserId, orderId, notes);
        return CancelOrderResponse.builder().orderId(orderId).build();
    }
}
