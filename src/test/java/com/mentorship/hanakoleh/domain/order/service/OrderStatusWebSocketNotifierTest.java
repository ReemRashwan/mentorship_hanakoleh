package com.mentorship.hanakoleh.domain.order.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.eq;

import com.mentorship.hanakoleh.domain.order.dto.OrderStatusWebSocketMessage;
import com.mentorship.hanakoleh.domain.order.event.OrderStatusChangedEvent;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class OrderStatusWebSocketNotifierTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Captor
    private ArgumentCaptor<OrderStatusWebSocketMessage> messageCaptor;

    @Test
    void sendsActiveOrderStatusToTheOrderTopic() {
        OrderStatusWebSocketNotifier notifier = new OrderStatusWebSocketNotifier(messagingTemplate);
        OffsetDateTime changedAt = OffsetDateTime.now();

        notifier.notifyCustomer(new OrderStatusChangedEvent(
                42L, 7, OrderFinalStatus.CONFIRMED, OrderFinalStatus.IN_PROGRESS, changedAt));

        verify(messagingTemplate).convertAndSend(
                eq("/topic/orders/42/status"), messageCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(
                new OrderStatusWebSocketMessage(
                        42L, OrderFinalStatus.CONFIRMED, OrderFinalStatus.IN_PROGRESS, changedAt),
                messageCaptor.getValue());
    }

    @Test
    void doesNotSendTerminalOrderStatus() {
        OrderStatusWebSocketNotifier notifier = new OrderStatusWebSocketNotifier(messagingTemplate);

        notifier.notifyCustomer(new OrderStatusChangedEvent(
                42L, 7, OrderFinalStatus.IN_DELIVERY, OrderFinalStatus.COMPLETED, OffsetDateTime.now()));

        verifyNoInteractions(messagingTemplate);
    }
}
