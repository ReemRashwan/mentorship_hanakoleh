package com.mentorship.hanakoleh.domain.order.repository;

import com.mentorship.hanakoleh.domain.order.model.Order;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {


    Page<Order> findByCustomerIdAndCreatedAtGreaterThanEqual(
            Integer customerId,
            OffsetDateTime createdFrom,
            Pageable pageable);

    @EntityGraph(attributePaths = "restaurant")
    List<Order> findByCustomer_IdAndFinalStatusNotInOrderByCreatedAtDesc(
            Integer customerId,
            Collection<OrderFinalStatus> statuses);

    @EntityGraph(attributePaths = "restaurant")
    Optional<Order> findByIdAndCustomer_Id(Long orderId, Integer userId);
    boolean existsByIdempotencyKey(UUID idempotencyKey);

}

