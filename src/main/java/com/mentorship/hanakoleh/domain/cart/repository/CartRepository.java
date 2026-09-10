package com.mentorship.hanakoleh.domain.cart.repository;

import com.mentorship.hanakoleh.domain.cart.model.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("""
            select c from Cart c
            left join fetch c.items i
            left join fetch i.menuItem
            where c.customer.id = :customerId
            """)
    Optional<Cart> findByCustomerIdForCheckout(@Param("customerId") Integer customerId);
}
