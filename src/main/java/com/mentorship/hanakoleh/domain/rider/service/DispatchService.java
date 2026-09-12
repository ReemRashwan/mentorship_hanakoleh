package com.mentorship.hanakoleh.domain.rider.service;

import com.mentorship.hanakoleh.domain.order.event.OrderConfirmedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class DispatchService {
    @TransactionalEventListener
    public void handlingIncomingConfirmedOrders(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("Incoming confirmed order event received. Contacting Rider...\nchecking Rider Status...\nrecording coordinates.");
    }
}
