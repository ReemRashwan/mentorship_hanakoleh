package com.mentorship.hanakoleh.domain.order.repository;

import com.mentorship.hanakoleh.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}