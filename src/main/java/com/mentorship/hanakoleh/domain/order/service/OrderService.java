package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.constants.OrderConstants;
import com.mentorship.hanakoleh.domain.order.dto.*;
import com.mentorship.hanakoleh.domain.order.event.OrderEvent;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.exception.OrderPersistenceException;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.OrderTracking;
import com.mentorship.hanakoleh.domain.order.projection.OrderItemLineCountProjection;
import com.mentorship.hanakoleh.domain.order.repository.OrderItemRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final List<OrderFinalStatus> NON_CURRENT_STATUSES = List.of(
            OrderFinalStatus.COMPLETED,
            OrderFinalStatus.CANCELLED,
            OrderFinalStatus.REFUNDED);
    private static Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderStatusUpdateService orderStatusUpdateService;
    private final OrderTrackingRepository orderTrackingRepository;
    private final ApplicationEventPublisher publisher;


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
        return orderRepository.findByCustomerIdAndFinalStatusNotInOrderByCreatedAtDesc(customerId, NON_CURRENT_STATUSES);
    }

    @Transactional(readOnly = true)
    public OrderDetails getOrder(Long orderId, Integer customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDetails(order, orderItemRepository.findByOrderIdOrderByIdAsc(orderId));
    }


    @Transactional
    public CancelOrderResponse cancelOrder(Long activeOrderId, Integer actorUserId, CancelOrderRequest cancelRequest) {
        Order activeOrder = findOrderByIdAndThrow(activeOrderId);
        OrderEvent cancelEvent = orderStatusUpdateService.cancelOrder(activeOrder, actorUserId, cancelRequest);

        persistOrderStatusUpdate(activeOrder, OrderFinalStatus.CANCELLED, actorUserId, cancelRequest.notes());
        publisher.publishEvent(cancelEvent);
        log.info("published Order {} is cancelled.", activeOrder.getId());
        return CancelOrderResponse.builder().orderId(activeOrderId).build();
    }

    @Transactional
    public UpdateOrderStatusResponse updateOrderStatus(Long activeOrderId, Integer actorUserId, UpdateOrderStatusRequest request) {
        Order activeOrder = findOrderByIdAndThrow(activeOrderId);

        OrderEvent event = switch (request.nextOrderStatus()) {
            case CONFIRMED -> orderStatusUpdateService.confirmOrder(activeOrder, actorUserId, request);
            case IN_PROGRESS -> orderStatusUpdateService.acceptOrder(activeOrder, actorUserId, request);
            case READY_FOR_PICKUP ->
                    orderStatusUpdateService.markOrderReadyForPickup(activeOrder, actorUserId, request);
            case IN_DELIVERY -> orderStatusUpdateService.pickupOrderByRider(activeOrder, actorUserId, request);
            case COMPLETED -> orderStatusUpdateService.deliverOrder(activeOrder, actorUserId, request);
            default ->
                    throw new IllegalArgumentException("Unsupported status update target: " + request.nextOrderStatus());
        };

        persistOrderStatusUpdate(activeOrder, request.nextOrderStatus(), actorUserId, request.notes());
        publisher.publishEvent(event);
        log.info(" Order Event {} for Order {} is published.", request.nextOrderStatus(), activeOrder.getId());

        return new UpdateOrderStatusResponse(activeOrder.getId(), activeOrder.getFinalStatus());
    }

    @Transactional // Fixed missing transaction
    public RefundOrderResponse refundOrder(Long activeOrderId, Integer actorUserId, RefundOrderRequest refundOrderRequest) {
        Order activeOrder = findOrderByIdAndThrow(activeOrderId);
        OrderEvent event = orderStatusUpdateService.refundOrder(activeOrder, refundOrderRequest);
        persistOrderStatusUpdate(activeOrder, OrderFinalStatus.REFUNDED, actorUserId, refundOrderRequest.notes());
        publisher.publishEvent(event);
        log.info(" Order Refunded for Order {} is published.", activeOrder.getId());
        return RefundOrderResponse.builder().orderId(activeOrderId).build();
    }

    private Order findOrderByIdAndThrow(Long orderId) {
        return findOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %d not found", orderId)));
    }

    private Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    private void persistOrderStatusUpdate(Order activeOrder, OrderFinalStatus newStatus, Integer actorUserId, String notes) {
        OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
        activeOrder.setFinalStatus(newStatus);
        activeOrder.setUpdatedAt(OffsetDateTime.now());

        try {
            orderRepository.save(activeOrder);
            log.info("Order {}  save Successfully .", activeOrder.getId());

        } catch (OptimisticLockingFailureException e) {
            throw new OrderPersistenceException("Optimistic locking failure");

        }

        persistOrderTrackingRecord(activeOrder, newStatus, actorUserId, notes, previousStatus);
    }

    private void persistOrderTrackingRecord(Order activeOrder, OrderFinalStatus newStatus, Integer actorUserId, String notes, OrderFinalStatus previousStatus) {
        orderTrackingRepository.save(OrderTracking.builder()
                .order(activeOrder)
                .currentStatus(newStatus)
                .previousStatus(previousStatus)
                .notes(notes)
                .triggeredByUserId(actorUserId)
                .createdAt(OffsetDateTime.now())
                .build());
    }

}
