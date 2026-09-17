package com.mentorship.hanakoleh.domain.order.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusUpdateService {
    private final ApplicationEventPublisher applicationEventPUblisher;

    public OrderStatusUpdateService(ApplicationEventPublisher applicationEventPUblisher) {
        this.applicationEventPUblisher = applicationEventPUblisher;
    }
}
