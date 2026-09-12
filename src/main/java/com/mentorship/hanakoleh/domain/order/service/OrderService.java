package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetails;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.repository.OrderItemRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import java.util.List;
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

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> getCurrentOrders(Integer customerId) {
        return orderRepository.findByCustomer_IdAndFinalStatusNotInOrderByCreatedAtDesc(
                customerId,
                NON_CURRENT_STATUSES);
    }

    @Transactional(readOnly = true)
    public OrderDetails getOrder(Long orderId, Integer customerId) {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, customerId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDetails(order, orderItemRepository.findByOrderIdOrderByIdAsc(orderId));
    }
}
