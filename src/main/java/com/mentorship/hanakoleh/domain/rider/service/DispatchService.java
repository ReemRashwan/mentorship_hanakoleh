package com.mentorship.hanakoleh.domain.rider.service;

import com.mentorship.hanakoleh.domain.order.event.OrderConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import com.mentorship.hanakoleh.domain.order.event.OrderAcceptedByRestaurantEvent;
import com.mentorship.hanakoleh.domain.order.event.OrderPreparedEvent;
import com.mentorship.hanakoleh.domain.order.event.OrderPickedUpEvent;
import com.mentorship.hanakoleh.domain.order.event.OrderDeliveredEvent;
import com.mentorship.hanakoleh.domain.order.service.OrderStatusUpdateService;

@Slf4j
@Service
public class DispatchService {
    private final OrderStatusUpdateService orderStatusUpdateService;

    public DispatchService(OrderStatusUpdateService orderStatusUpdateService) {
        this.orderStatusUpdateService = orderStatusUpdateService;
    }

    @TransactionalEventListener
    public void handlingIncomingConfirmedOrders(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("Incoming confirmed order event received. Contacting Rider...\nchecking Rider Status...\nrecording coordinates.");
    }



    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleIncomingConfirmedOrders(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("Incoming Order {} that is {} is received. \n1-order noted for future courier planning in this delivery zone. \n2-no courier assignment yet, restaurant has not accepted.",
                orderConfirmedEvent.getOrderId(), orderConfirmedEvent.getOrderFinalStatus());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleIncomingAcceptedOrders(OrderAcceptedByRestaurantEvent orderAcceptedByRestaurantEvent) {
        log.info("Order {} has been accepted by restaurant and is now IN_PROGRESS. \n1-estimating prep time to begin courier pre-assignment.\n2-not yet eligible for pickup.",
                orderAcceptedByRestaurantEvent.getOrderId());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleIncomingPreparedOrders(OrderPreparedEvent orderPreparedEvent) {
        log.info("Order {} that is {} is received. \n1-order is READY_FOR_PICKUP, matching to nearest available courier.\n2-pushing pickup task to assigned courier's app.",
                orderPreparedEvent.getOrderId(), orderPreparedEvent.getOrderFinalStatus());
    }

}

