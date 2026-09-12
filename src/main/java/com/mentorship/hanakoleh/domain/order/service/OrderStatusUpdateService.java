package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.event.*;
import com.mentorship.hanakoleh.domain.order.exception.InvalidOrderTransitionException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotOwnedByRestaurantException;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.OrderTracking;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderTrackingRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@Service
@Slf4j
public class OrderStatusUpdateService {
    private final ApplicationEventPublisher publisher;
    private final OrderRepository orderRepository;
    private final OrderTrackingRepository orderTrackingRepository;

    @Transactional
    boolean confirmOrder(Order activeOrder, String notes) {

        if (checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.CONFIRMED)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.IN_PROGRESS);
        }
            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.CONFIRMED);
            activeOrder.setUpdatedAt(OffsetDateTime.now());
            orderRepository.save(activeOrder);
            persistOrderTrackingRecord(activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to CONFIRMED.");


            publisher.publishEvent(OrderConfirmedEvent.builder().orderId(activeOrder.getId())
                    .orderPaymentMethod(activeOrder.getPaymentMethod())
                    .orderPaymentStatus(activeOrder.getPaymentStatus()).
                    orderFinalStatus(OrderFinalStatus.CONFIRMED).build());
            log.info("published Order CONFIRMED.");
            return true;
    }

    @Transactional
    public boolean acceptOrder( Integer authenticatedRestaurantId, Integer activeOrderId,String notes) {
        Order activeOrder = orderRepository.findById(activeOrderId).orElseThrow(
                () ->   new OrderNotFoundException(String.format("Order with id %d not found", activeOrderId)));

        if (!activeOrder.getRestaurant().getId().equals(authenticatedRestaurantId)) {
            throw new OrderNotOwnedByRestaurantException(activeOrderId, authenticatedRestaurantId);
        }

        if (!checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.IN_PROGRESS)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.IN_PROGRESS);
        }

            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.IN_PROGRESS);
        activeOrder.setUpdatedAt(OffsetDateTime.now());

        orderRepository.save(activeOrder);
            persistOrderTrackingRecord(activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to IN_PROGRESS.");

            publisher.publishEvent(OrderAcceptedByRestaurantEvent.builder().orderId(activeOrder.getId()).build());
            log.info("Order {} accepted by restaurant {}", activeOrder.getId(), authenticatedRestaurantId);

            return true;

    }

    @Transactional
    boolean markOrderReadyForPickup(Order activeOrder, String notes) {
        if (checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.READY_FOR_PICKUP)) {
            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.READY_FOR_PICKUP);
            activeOrder.setUpdatedAt(OffsetDateTime.now());
            orderRepository.save(activeOrder);
            persistOrderTrackingRecord(activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to READY_FOR_PICKUP.");
            publisher.publishEvent(OrderMarkedReadyForDeliveryEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order marked READY_FOR_PICKUP.");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean pickupOrder(Order activeOrder, String notes) {
        if (checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.IN_DELIVERY)) {
            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.IN_DELIVERY);
            activeOrder.setUpdatedAt(OffsetDateTime.now());

            orderRepository.save(activeOrder);
            persistOrderTrackingRecord(activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to In_Delivery.");
            publisher.publishEvent(OrderPickedUpEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order PICKED UP.");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean deliverOrder(Order activeOrder, String notes) {
        if (checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.COMPLETED)) {
            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.COMPLETED);
            activeOrder.setUpdatedAt(OffsetDateTime.now());
            orderRepository.save(activeOrder);
            persistOrderTrackingRecord(activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to COMPLETED.");
            publisher.publishEvent(OrderConfirmedEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order DELIVERED");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean cancelOrder(Order activeOrder, String notes) {
        if (checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.CANCELLED)) {
            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.CANCELLED);
            activeOrder.setUpdatedAt(OffsetDateTime.now());
            orderRepository.save(activeOrder);
            persistOrderTrackingRecord(activeOrder, previousStatus, notes);
            log.info("Order status updated successfully to CANCELLED.");
            publisher.publishEvent(OrderCancelledEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order CANCELLED.");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean refundOrder(Order activeOrder, String notes) {
        if (checkAbilityToUpdateOrderStatus(activeOrder, OrderFinalStatus.REFUNDED)) {
            OrderFinalStatus previousStatus = activeOrder.getFinalStatus();
            activeOrder.setFinalStatus(OrderFinalStatus.REFUNDED);
            activeOrder.setUpdatedAt(OffsetDateTime.now());

            orderRepository.save(activeOrder);
            persistOrderTrackingRecord(activeOrder,previousStatus,notes);
            log.info("Order status updated successfully to REFUNDED.");
            publisher.publishEvent(OrderMarkedReadyForDeliveryEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order REFUND PROCESSED.");
            return true;
        } else
            return false;

    }

    private boolean checkAbilityToUpdateOrderStatus(Order activeOrder, OrderFinalStatus nextStatus) {
        return  activeOrder.getFinalStatus().canTransitionTo(nextStatus);
    }

    private void persistOrderTrackingRecord(Order activeOrder, OrderFinalStatus previousStatus, String notes) {
        orderTrackingRepository.save(OrderTracking
                .builder()
                .order(activeOrder)
                .currentStatus(activeOrder.getFinalStatus())
                .previousStatus(previousStatus)
                .notes(notes)
                .triggeredByCustomerId(activeOrder.getCustomer().getId())
                .createdAt(OffsetDateTime.now())
                .build());
    }

}
