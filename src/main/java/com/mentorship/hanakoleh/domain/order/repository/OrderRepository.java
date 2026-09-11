package com.mentorship.hanakoleh.domain.order.repository;

import com.mentorship.hanakoleh.domain.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository <Order,Integer> {
    boolean existsByIdempotencyKey(UUID idempotencyKey);
}
