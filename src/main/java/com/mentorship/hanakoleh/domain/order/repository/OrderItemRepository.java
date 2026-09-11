package com.mentorship.hanakoleh.domain.order.repository;

import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}