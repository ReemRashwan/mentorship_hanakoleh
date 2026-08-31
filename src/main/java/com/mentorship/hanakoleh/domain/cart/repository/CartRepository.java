package com.mentorship.hanakoleh.domain.cart.repository;


import com.mentorship.hanakoleh.domain.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByCustomerId(Integer customerId);

    boolean existsByCustomerId(Integer customerId);
}
