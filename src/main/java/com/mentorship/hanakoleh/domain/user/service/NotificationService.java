package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.order.event.OrderAcceptedByRestaurantEvent;
import com.mentorship.hanakoleh.domain.order.event.OrderConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class NotificationService {
    @TransactionalEventListener
    public void handleConfirmedOrder(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("Incoming confirmed order event received. notifying customer...\n notifying restaurant\n ");
    }
    @TransactionalEventListener
    public void handleAcceptedOrder(OrderAcceptedByRestaurantEvent  orderAcceptedEvent) {
        log.info("Order "+orderAcceptedEvent.getOrderId()+" accepted by restaurant...preparing started!!!");
    }
}
