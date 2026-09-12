package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.checkout.exception.PaymentNotSatisfiedException;
import com.mentorship.hanakoleh.domain.order.event.*;
import com.mentorship.hanakoleh.domain.order.exception.InvalidOrderTransitionException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotOwnedByRestaurantException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotOwnedByRiderException;
import com.mentorship.hanakoleh.domain.order.model.*;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderTrackingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Service
@Slf4j
public class OrderStatusUpdateService {
    private final ApplicationEventPublisher publisher;
    private final OrderRepository orderRepository;
    private final OrderTrackingRepository orderTrackingRepository;

    @Transactional
    public void confirmOrder(Long activeOrderId, String notes) {
        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));

        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.CONFIRMED)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.CONFIRMED);
        }

        if (!isPaymentSatisfiedForConfirmation(activeOrder)) {
            throw new PaymentNotSatisfiedException(activeOrder.getId(), activeOrder.getPaymentMethod(), activeOrder.getPaymentStatus());
        }
        OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
        activeOrder.setFinalStatus(OrderFinalStatus.CONFIRMED);
        activeOrder.setUpdatedAt(OffsetDateTime.now());
        try {
            orderRepository.save(activeOrder);
        } catch (OptimisticLockingFailureException e) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.CONFIRMED);
        }
        persistOrderTrackingRecord(activeOrder.getCustomer().getUser().getId(),activeOrder, previousStatus, notes);
        log.info("Order {} status updated to CONFIRMED.", activeOrder.getId());


        publisher.publishEvent(OrderConfirmedEvent.builder().orderId(activeOrder.getId())
                .orderPaymentMethod(activeOrder.getPaymentMethod())
                .orderPaymentStatus(activeOrder.getPaymentStatus()).
                orderFinalStatus(OrderFinalStatus.CONFIRMED).build());
        log.info("published Order CONFIRMED event for order {}.", activeOrder.getId());
    }

    @Transactional
    public void acceptOrder(Integer authenticatedRestaurantId, Long activeOrderId, String notes) {
        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));

        if (!activeOrder.getRestaurant().getId().equals(authenticatedRestaurantId)) {
            throw new OrderNotOwnedByRestaurantException(activeOrderId, authenticatedRestaurantId);
        }

        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.IN_PROGRESS)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.IN_PROGRESS);
        }

        OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
        activeOrder.setFinalStatus(OrderFinalStatus.IN_PROGRESS);
        activeOrder.setUpdatedAt(OffsetDateTime.now());

        try {
            orderRepository.save(activeOrder);
        } catch (OptimisticLockingFailureException e) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.IN_PROGRESS);
        }

        persistOrderTrackingRecord(activeOrder.getRestaurant().getId(),activeOrder, previousStatus, notes);
        log.info("Order {} status updated to IN_PROGRESS.", activeOrder.getId());

        publisher.publishEvent(OrderAcceptedByRestaurantEvent.builder().orderId(activeOrder.getId()).build());
        log.info("Order {} accepted by restaurant {}", activeOrder.getId(), authenticatedRestaurantId);
    }

    @Transactional
    void markOrderReadyForPickup(Integer authenticatedRestaurantId, Long activeOrderId, String notes) {

        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));

        if (!activeOrder.getRestaurant().getId().equals(authenticatedRestaurantId)) {
            throw new OrderNotOwnedByRestaurantException(activeOrderId, authenticatedRestaurantId);
        }
        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.READY_FOR_PICKUP)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.READY_FOR_PICKUP);
        }

            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.READY_FOR_PICKUP);
            activeOrder.setUpdatedAt(OffsetDateTime.now());
        try {
            orderRepository.save(activeOrder);
        } catch (OptimisticLockingFailureException e) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.READY_FOR_PICKUP);
        }
            persistOrderTrackingRecord(activeOrder.getRestaurant().getId(),activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to READY_FOR_PICKUP.");
            publisher.publishEvent(OrderPreparedEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order marked READY_FOR_PICKUP.");
    }

    @Transactional
    public void pickupOrderByRider(Long authenticatedRiderId, Long activeOrderId, String notes) {
        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));

        if (!activeOrder.getRider().getId().equals(authenticatedRiderId)) {
            throw new OrderNotOwnedByRiderException(activeOrderId, authenticatedRiderId);
        }
        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.IN_DELIVERY)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.IN_DELIVERY);
        }
        OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
        activeOrder.setFinalStatus(OrderFinalStatus.IN_DELIVERY);
        activeOrder.setUpdatedAt(OffsetDateTime.now());

        try {
            orderRepository.save(activeOrder);
        } catch (OptimisticLockingFailureException e) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.IN_DELIVERY);
        }
        persistOrderTrackingRecord(Math.toIntExact(activeOrder.getRider().getId()),activeOrder, previousStatus, notes);
        log.info("Order status updated successfully to In_Delivery.");
        publisher.publishEvent(OrderPickedUpEvent.builder().orderId(activeOrder.getId()).build());
        log.info("published Order PICKED UP.");

    }

    @Transactional
    public void deliverOrder(Long authenticatedRiderId, Long activeOrderId, boolean paymentCollected, String notes) {
        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));
        if (!activeOrder.getRider().getId().equals(authenticatedRiderId)) {
            throw new OrderNotOwnedByRiderException(activeOrderId, authenticatedRiderId);
        }
        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.COMPLETED)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.COMPLETED);
        }
        if (activeOrder.getPaymentMethod() == OrderPaymentMethod.CASH && !paymentCollected) {
            throw new PaymentNotSatisfiedException(activeOrder.getId(), activeOrder.getPaymentMethod(), activeOrder.getPaymentStatus());
        }

        OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.COMPLETED);
            activeOrder.setUpdatedAt(OffsetDateTime.now());
        try {
            orderRepository.save(activeOrder);
        } catch (OptimisticLockingFailureException e) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.COMPLETED);
        }
            persistOrderTrackingRecord(Math.toIntExact(activeOrder.getRider().getId()),activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to COMPLETED.");
            publisher.publishEvent(OrderDeliveredEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order DELIVERED");

    }

    @Transactional
    public void cancelOrder(Integer cancelingActor, Long activeOrderId, String notes) {
        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));

        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.CANCELLED)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.CANCELLED);
        }

            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.CANCELLED);
            activeOrder.setUpdatedAt(OffsetDateTime.now());
        try {
            orderRepository.save(activeOrder);
        } catch (OptimisticLockingFailureException e) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.CANCELLED);
        }
            persistOrderTrackingRecord(cancelingActor,activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to CANCELLED.");
            publisher.publishEvent(OrderCancelledEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order CANCELLED.");

    }

    @Transactional
    public void refundOrder(Integer refundingActor, Long activeOrderId, String notes) {
        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () -> new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));

        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.REFUNDED)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.REFUNDED);
        }
        OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.REFUNDED);
            activeOrder.setUpdatedAt(OffsetDateTime.now());

            try {
                orderRepository.save(activeOrder);
            } catch (OptimisticLockingFailureException e) {
                throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.REFUNDED);
            }
            persistOrderTrackingRecord(refundingActor, activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to REFUNDED.");
            publisher.publishEvent(OrderRefundProcessedEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order REFUND PROCESSED.");

    }

    private boolean checkAbilityToUpdateOrderStatus(Order activeOrder, OrderFinalStatus nextStatus) {
        return activeOrder.getFinalStatus().canTransitionTo(nextStatus);
    }

    private boolean isPaymentSatisfiedForConfirmation(Order activeOrder) {
        var paymentMethod = activeOrder.getPaymentMethod();
        var isOnline = List.of(OrderPaymentMethod.CREDIT_CARD, OrderPaymentMethod.DEBIT_CARD, OrderPaymentMethod.WALLET).contains(paymentMethod);
        return (paymentMethod == OrderPaymentMethod.CASH || (isOnline && activeOrder.getPaymentStatus() == OrderPaymentStatus.PAID));

    }

    private void persistOrderTrackingRecord(Integer actorUserId, Order activeOrder, OrderFinalStatus previousStatus, String notes) {
        orderTrackingRepository.save(OrderTracking
                .builder()
                .order(activeOrder)
                .currentStatus(activeOrder.getFinalStatus())
                .previousStatus(previousStatus)
                .notes(notes)
                .triggeredByUserId(actorUserId)
                .createdAt(OffsetDateTime.now())
                .build());
    }

}
