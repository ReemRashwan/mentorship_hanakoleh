package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.order.event.OrderConfirmedEvent;
import com.mentorship.hanakoleh.domain.order.service.OrderStatusUpdateService;
import com.mentorship.hanakoleh.domain.restaurant.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
@Slf4j
public class RestaurantService {

    private final OrderStatusUpdateService orderStatusUpdateService;

    public RestaurantService(OrderStatusUpdateService orderStatusUpdateService) {
        this.orderStatusUpdateService = orderStatusUpdateService;
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleIncomingOrders(OrderConfirmedEvent  orderConfirmedEvent) {
       //ord
    }

    public void acceptOrderByRestaurant(Integer authenticatedRestaurantId, Integer orderId, String notes) {
        orderStatusUpdateService.acceptOrder(authenticatedRestaurantId, orderId, notes);
    }


}
