package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.constants.OrderConstants;
import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetails;
import com.mentorship.hanakoleh.domain.order.model.Order;
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

    private static final List<OrderFinalStatus> NON_CURRENT_STATUSES = List.of(
            OrderFinalStatus.COMPLETED,
            OrderFinalStatus.CANCELLED,
            OrderFinalStatus.REFUNDED);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderMapper orderMapper) {
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryResponse> getHistoricalOrders(
            Integer customerId,
            Pageable pageable) {
        OffsetDateTime startDate = getHistoricalOrderStartDate();
        Page<Order> orders = orderRepository.findByCustomerIdAndCreatedAtGreaterThanEqual(customerId, startDate, pageable);
        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> orderIds = orders.getContent().stream()
        .map(Order::getId)
        .toList();

        List<OrderItemLineCountProjection> lineCounts =
        orderItemRepository.findLineCountByOrderIds(orderIds);

        Map<Long, Long> lineCountByOrderId = lineCounts.stream()
        .collect(Collectors.toMap(
                OrderItemLineCountProjection::getOrderId,
                OrderItemLineCountProjection::getLineCount));

        return orders.map(order -> {
    long lineCount = lineCountByOrderId.getOrDefault(order.getId(), 0L);

    return orderMapper.toOrderHistoryResponse(order, lineCount);
});

    public List<Order> getCurrentOrders(Integer customerId) {
        return orderRepository.findByCustomer_IdAndFinalStatusNotInOrderByCreatedAtDesc(
                customerId,
                NON_CURRENT_STATUSES);
    }

    // Helper methods
    // Helper method to get the start date for the historical orders
    private OffsetDateTime getHistoricalOrderStartDate() {
        return OffsetDateTime.now().minusMonths(OrderConstants.NUMBER_OF_MONTHS_FOR_HISTORICAL_ORDERS);
    @Transactional(readOnly = true)
    public OrderDetails getOrder(Long orderId, Integer customerId) {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, customerId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDetails(order, orderItemRepository.findByOrderIdOrderByIdAsc(orderId));
    }

}
