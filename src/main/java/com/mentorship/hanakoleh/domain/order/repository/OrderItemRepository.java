package com.mentorship.hanakoleh.domain.order.repository;

import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import com.mentorship.hanakoleh.domain.order.projection.OrderItemLineCountProjection;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    //long countByOrderId(Long orderId);

    @Query("""
        SELECT oi.order.id AS orderId, COUNT(oi.id) AS lineCount
        FROM OrderItem oi
        WHERE oi.order.id IN :orderIds
        GROUP BY oi.order.id
    """)
    List<OrderItemLineCountProjection> findLineCountByOrderIds(
        @Param("orderIds") Collection<Long> orderIds);

    List<OrderItem> findByOrderIdOrderByIdAsc(Long orderId);
}
