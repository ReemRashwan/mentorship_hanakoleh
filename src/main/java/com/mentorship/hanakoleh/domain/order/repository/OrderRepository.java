package com.mentorship.hanakoleh.domain.order.repository;

import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderFinalStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "restaurant")
    List<Order> findByCustomer_IdAndFinalStatusNotInOrderByCreatedAtDesc(
            Integer customerId,
            Collection<OrderFinalStatus> statuses);

    @EntityGraph(attributePaths = "restaurant")
    Optional<Order> findByIdAndCustomer_Id(Long orderId, Integer userId);
}
