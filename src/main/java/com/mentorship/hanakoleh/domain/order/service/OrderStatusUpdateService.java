package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.event.*;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderTrackingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
@Service
@Slf4j
public class OrderStatusUpdateService  {
    private OrderRepository orderRepository;
    private OrderTrackingRepository orderTrackingRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    boolean confirmOrder(Order activeOrder){
            if(checkAbilityToUpdateOrderStatus(activeOrder,OrderFinalStatus.CONFIRMED)){
                activeOrder.setFinalStatus(OrderFinalStatus.CONFIRMED);
                orderRepository.save(activeOrder);
                log.info("Order status updated successfully to CONFIRMED.");
                publisher.publishEvent(OrderConfirmedEvent.builder().orderId(activeOrder.getId())
                        .orderPaymentMethod(activeOrder.getPaymentMethod())
                        .orderPaymentStatus(activeOrder.getPaymentStatus()).
                        orderFinalStatus(OrderFinalStatus.CONFIRMED).build());
                log.info("published Order CONFIRMED.");
                return true;
            }
            else
                return false;
    }

    @Transactional
    boolean acceptOrderByRestaurant(Order activeOrder){
        if(checkAbilityToUpdateOrderStatus(activeOrder,OrderFinalStatus.IN_PROGRESS)){
            activeOrder.setFinalStatus(OrderFinalStatus.IN_PROGRESS);
            orderRepository.save(activeOrder);
            //need to enter order tracking history
            //OrderTracking orderTrackingRecord = OrderTracking.builder().order(activeOrder).status().notes().createdByUserId().createdAt().build()
            log.info("Order status updated successfully to IN_PROGRESS.");
            publisher.publishEvent(OrderAcceptedByRestaurantEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order ACCEPTED by Restaurant.");
        return true;
        }
        else
            return false;
    }

    @Transactional
    boolean markOrderReadyForPickup(Order activeOrder){
        if(checkAbilityToUpdateOrderStatus(activeOrder,OrderFinalStatus.READY_FOR_PICKUP))
        {
            activeOrder.setFinalStatus(OrderFinalStatus.READY_FOR_PICKUP);
            orderRepository.save(activeOrder);
            log.info("Order status updated successfully to READY_FOR_PICKUP.");
            publisher.publishEvent(OrderMarkedReadyForDeliveryEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order marked READY_FOR_PICKUP.");
            return true;
        } else
      return false;

    }

    @Transactional
    boolean pickupOrder(Order activeOrder){
        if(checkAbilityToUpdateOrderStatus(activeOrder,OrderFinalStatus.IN_DELIVERY))
        {
            activeOrder.setFinalStatus(OrderFinalStatus.IN_DELIVERY);
            orderRepository.save(activeOrder);
            log.info("Order status updated successfully to In_Delivery.");
            publisher.publishEvent(OrderPickedUpEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order PICKED UP.");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean deliverOrder(Order activeOrder){
        if(checkAbilityToUpdateOrderStatus(activeOrder,OrderFinalStatus.COMPLETED))
        {
            activeOrder.setFinalStatus(OrderFinalStatus.COMPLETED);
            orderRepository.save(activeOrder);
            log.info("Order status updated successfully to COMPLETED.");
            publisher.publishEvent(OrderConfirmedEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order DELIVERED");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean cancelOrder(Order activeOrder){
        if(checkAbilityToUpdateOrderStatus(activeOrder,OrderFinalStatus.CANCELLED))
        {
            activeOrder.setFinalStatus(OrderFinalStatus.CANCELLED);
            orderRepository.save(activeOrder);
            log.info("Order status updated successfully to CANCELLED.");
            publisher.publishEvent(OrderCancelledEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order CANCELLED.");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean refundOrder(Order activeOrder){
        if(checkAbilityToUpdateOrderStatus(activeOrder,OrderFinalStatus.REFUNDED))
        {
            activeOrder.setFinalStatus(OrderFinalStatus.REFUNDED);
            orderRepository.save(activeOrder);
            log.info("Order status updated successfully to REFUNDED.");
            publisher.publishEvent(OrderMarkedReadyForDeliveryEvent.builder().orderId(activeOrder.getId()).build());
            log.info("published Order REFUND PROCESSED.");
            return true;
        } else
            return false;

    }

    @Transactional
    boolean checkAbilityToUpdateOrderStatus(Order activeOrder, OrderFinalStatus nextStatus){
        return !orderRepository.existsByIdempotencyKey(activeOrder.getIdempotencyKey()) || activeOrder.getFinalStatus().canTransitionTo(nextStatus);
    }

}
