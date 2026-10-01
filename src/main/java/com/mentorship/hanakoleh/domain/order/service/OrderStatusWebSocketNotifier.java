package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.dto.OrderStatusWebSocketMessage;
import com.mentorship.hanakoleh.domain.order.event.OrderStatusChangedEvent;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import java.util.EnumSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderStatusWebSocketNotifier {

    private static final Set<OrderFinalStatus> TERMINAL_STATUSES = EnumSet.of(
            OrderFinalStatus.COMPLETED,
            OrderFinalStatus.CANCELLED,
            OrderFinalStatus.REFUNDED);

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyCustomer(OrderStatusChangedEvent event) {
        if (TERMINAL_STATUSES.contains(event.currentStatus())) {
            return;
        }

        messagingTemplate.convertAndSend(
                "/topic/orders/" + event.orderId() + "/status",
                new OrderStatusWebSocketMessage(
                        event.orderId(),
                        event.previousStatus(),
                        event.currentStatus(),
                        event.changedAt()));
    }
}
