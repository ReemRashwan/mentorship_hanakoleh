package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.checkout.exception.PaymentNotSatisfiedException;
import com.mentorship.hanakoleh.domain.order.exception.*;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus.CANCELLED;
import static com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus.CONFIRMED;
import static com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus.CREATED;
import static com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus.IN_PROGRESS;

@RequiredArgsConstructor
@Component
public class OrderStatusTransitionControl {

    public static final int IN_PROGRESS_BUFFER_TIME = 15;
    public static final int READY_FOR_PICKUP_BUFFER_TIME = 25;
    public static final int IN_DELIVERY_BUFFER_TIME = 25;

    public void checkTransitionAbilityGuard(Order activeOrder, OrderFinalStatus nextOrderStatus) {
        if (!activeOrder.getFinalStatus().canTransitionTo(nextOrderStatus)) {
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), nextOrderStatus);
        }
    }

    public void checkSuccessfulPaymentGuard(Order activeOrder) {
        var paymentMethod = activeOrder.getPaymentMethod();
        var isOnline = List.of(OrderPaymentMethod.CREDIT_CARD, OrderPaymentMethod.DEBIT_CARD, OrderPaymentMethod.WALLET).contains(paymentMethod);
        if (!(paymentMethod == OrderPaymentMethod.CASH || (isOnline && activeOrder.getPaymentStatus() == OrderPaymentStatus.PAID))) {
            throw new PaymentNotSatisfiedException(activeOrder.getId(), activeOrder.getPaymentMethod(), activeOrder.getPaymentStatus());
        }
    }

    public void checkRestaurantOwnershipGuard(Order activeOrder, Integer authenticatedRestaurantId) {
        if (!activeOrder.getRestaurant().getId().equals(authenticatedRestaurantId)) {
            throw new OrderNotOwnedByRestaurantException(activeOrder.getId(), authenticatedRestaurantId);
        }
    }

    public void checkCustomerOwnershipGuard(Order activeOrder, Integer authenticatedCustomerId) {
        if (!activeOrder.getCustomer().getId().equals(authenticatedCustomerId)) {
            throw new OrderNotOwnedByCustomerException(activeOrder.getId(), authenticatedCustomerId);
        }
    }

    public void checkRiderOwnershipGuard(Order activeOrder, Long authenticatedRiderId) {
        if (!activeOrder.getRider().getId().equals(authenticatedRiderId)) {
            throw new OrderNotOwnedByRiderException(activeOrder.getId(), authenticatedRiderId);
        }
    }

    public void checkCashPaymentCollectionGuard(Order activeOrder, boolean cashPaymentCollected) {
        if (activeOrder.getPaymentMethod() == OrderPaymentMethod.CASH && !cashPaymentCollected) {
            throw new PaymentNotSatisfiedException(activeOrder.getId(), activeOrder.getPaymentMethod(), activeOrder.getPaymentStatus());
        }

    }

    public void checkCustomerCancellationAbilityGuard(Order activeOrder) {
        Predicate<OrderFinalStatus> testCancellationAbility = status ->
                Set.of(CREATED, CONFIRMED).contains(status) && status.canTransitionTo(CANCELLED);
        if (!testCancellationAbility.test(activeOrder.getFinalStatus()))
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.CANCELLED);
    }

    public void checkRestaurantCancellationAbilityGuard(Order activeOrder, String reason) {
        Predicate<String> testRestaurantReason = (String str) -> str != null && !str.isBlank();
        Predicate<OrderFinalStatus> testCancellationAbility = status ->
                Set.of(CREATED, CONFIRMED, IN_PROGRESS).contains(status) && status.canTransitionTo(CANCELLED);

        if (!testCancellationAbility.test(activeOrder.getFinalStatus()) || !testRestaurantReason.test(reason))
            throw new InvalidOrderTransitionException(activeOrder.getFinalStatus(), OrderFinalStatus.CANCELLED);
    }

    public void checkSLABreachGuard(Order activeOrder) {
        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime startBaseline = switch (activeOrder.getFinalStatus()) {
            case IN_PROGRESS, READY_FOR_PICKUP ->
                    activeOrder.getUpdatedAt() != null ? activeOrder.getUpdatedAt() : activeOrder.getCreatedAt();
            case IN_DELIVERY -> activeOrder.getCreatedAt();
            default -> activeOrder.getCreatedAt();
        };

        Duration threshold = switch (activeOrder.getFinalStatus()) {
            case IN_PROGRESS -> {
                int prepTime = activeOrder.getRestaurant().getAvgPreparationTimeInMins();
                yield Duration.ofMinutes(IN_PROGRESS_BUFFER_TIME + prepTime);
            }
            case READY_FOR_PICKUP -> Duration.ofMinutes(READY_FOR_PICKUP_BUFFER_TIME);
            case IN_DELIVERY -> {
                OffsetDateTime eta = activeOrder.getEstimatedDeliveryAt();
                if (eta == null) {
                    throw new IllegalStateException("Estimated delivery time is missing for order in IN_DELIVERY status");
                }
                // Total allowed delivery duration: (ETA - CreatedAt) + 25 minutes buffer
                yield Duration.between(activeOrder.getCreatedAt(), eta).plusMinutes(IN_DELIVERY_BUFFER_TIME);
            }
            default -> throw new InvalidOrderTransitionException(
                    activeOrder.getFinalStatus(),
                    OrderFinalStatus.CANCELLED
            );
        };

        Duration elapsed = Duration.between(startBaseline, now);

        if (elapsed.compareTo(threshold) < 0) {
            throw new SlaNotBreachedException(activeOrder.getId(), activeOrder.getFinalStatus(), elapsed, threshold);
        }
    }


}
