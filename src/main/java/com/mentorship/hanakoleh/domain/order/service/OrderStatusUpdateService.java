package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.dto.CancelOrderRequest;
import com.mentorship.hanakoleh.domain.order.dto.RefundOrderRequest;
import com.mentorship.hanakoleh.domain.order.dto.UpdateOrderStatusRequest;
import com.mentorship.hanakoleh.domain.order.event.*;
import com.mentorship.hanakoleh.domain.order.model.*;
import com.mentorship.hanakoleh.domain.rider.service.DispatchService;
import com.mentorship.hanakoleh.domain.user.model.Rider;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class OrderStatusUpdateService {

    private final OrderStatusTransitionControl transitionControl;
    private final CustomerService customerService;
    private final DispatchService dispatchService;

    public OrderEvent confirmOrder(Order activeOrder, Integer actorUserId, UpdateOrderStatusRequest updateStatusRequest) {
        transitionControl.checkTransitionAbilityGuard(activeOrder, updateStatusRequest.nextOrderStatus());
        transitionControl.checkSuccessfulPaymentGuard(activeOrder);
        Integer customerId = customerService.retrieveCustomerIdByUserId(actorUserId);
        transitionControl.checkCustomerOwnershipGuard(activeOrder, customerId);
        return OrderConfirmedEvent.builder().orderId(activeOrder.getId())
                .orderPaymentMethod(activeOrder.getPaymentMethod())
                .orderPaymentStatus(activeOrder.getPaymentStatus())
                .notes(updateStatusRequest.notes())
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId)
                .finalStatus(OrderFinalStatus.CONFIRMED).build();
    }

    public OrderEvent acceptOrder(Order activeOrder, Integer actorUserId, UpdateOrderStatusRequest updateStatusRequest) {
        transitionControl.checkTransitionAbilityGuard(activeOrder, updateStatusRequest.nextOrderStatus());
        transitionControl.checkRestaurantOwnershipGuard(activeOrder, updateStatusRequest.restaurantId());
        return OrderAcceptedByRestaurantEvent.builder().orderId(activeOrder.getId())
                .finalStatus(OrderFinalStatus.IN_PROGRESS)
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId)
                .build();
    }

    OrderEvent markOrderReadyForPickup(Order activeOrder, Integer actorUserId, UpdateOrderStatusRequest updateStatusRequest) {
        transitionControl.checkTransitionAbilityGuard(activeOrder, updateStatusRequest.nextOrderStatus());
        transitionControl.checkRestaurantOwnershipGuard(activeOrder, updateStatusRequest.restaurantId());
        return OrderPreparedEvent.builder().orderId(activeOrder.getId())
                .finalStatus(OrderFinalStatus.READY_FOR_PICKUP)
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId).build();
    }

    public OrderEvent pickupOrderByRider(Order activeOrder, Integer actorUserId, UpdateOrderStatusRequest updateStatusRequest) {
        Rider authenticatedRider = dispatchService.retrieveRiderByUserId(actorUserId);
        transitionControl.checkRiderOwnershipGuard(activeOrder, authenticatedRider.getId());
        return OrderPickedUpEvent.builder().orderId(activeOrder.getId())
                .finalStatus(updateStatusRequest.nextOrderStatus())
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId).build();
    }

    public OrderEvent deliverOrder(Order activeOrder,Integer actorUserId, UpdateOrderStatusRequest updateStatusRequest) {
        transitionControl.checkTransitionAbilityGuard(activeOrder, updateStatusRequest.nextOrderStatus());
        Rider authenticatedRider = dispatchService.retrieveRiderByUserId(actorUserId);
        transitionControl.checkRiderOwnershipGuard(activeOrder, authenticatedRider.getId());
        transitionControl.checkCashPaymentCollectionGuard(activeOrder, updateStatusRequest.cashPaymentCollected());
        return OrderDeliveredEvent.builder()
                .orderId(activeOrder.getId())
                .finalStatus(updateStatusRequest.nextOrderStatus())
                .eventTrigger(updateStatusRequest.eventTrigger())
                .actorUserId(actorUserId).build();
    }

    public OrderEvent cancelOrder(Order activeOrder,Integer actorUserId, CancelOrderRequest cancelRequest) {
        transitionControl.checkTransitionAbilityGuard(activeOrder,OrderFinalStatus.CANCELLED);
        OrderCancellationTrigger cancellationTrigger = cancelRequest.cancellationTrigger();
        String reason = cancelRequest.reason();
        switch (cancellationTrigger) {
            case CUSTOMER_CANCELLED -> {
                transitionControl.checkCustomerOwnershipGuard(activeOrder, actorUserId);
                transitionControl.checkCustomerCancellationAbilityGuard(activeOrder);
            }
            case RESTAURANT_CANCELLED -> {
                transitionControl.checkRestaurantOwnershipGuard(activeOrder, cancelRequest.restaurantId());
                if (reason == null || reason.isBlank()) {
                    throw new IllegalArgumentException("Reason is required for restaurant emergency cancellation");
                }
                transitionControl.checkRestaurantCancellationAbilityGuard(activeOrder, reason);
            }
            case SLA_BREACH -> {
                transitionControl.checkSLABreachGuard(activeOrder);
            }
        }
        return OrderCancelledEvent.builder().orderId(activeOrder.getId())
                .finalStatus(OrderFinalStatus.CANCELLED)
                .eventTrigger(cancelRequest.cancellationTrigger().name())
                .reason(cancelRequest.reason())
                .actorUserId(actorUserId).build();
    }

    public OrderEvent refundOrder(Order activeOrder, RefundOrderRequest refundOrderRequest) {
        transitionControl.checkTransitionAbilityGuard(activeOrder,OrderFinalStatus.REFUNDED);
        return OrderRefundProcessedEvent.builder().orderId(activeOrder.getId()).build();
    }


}
