package com.mentorship.hanakoleh.domain.cart.repository;

import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByCustomerId(Integer customerId);

    Optional<Cart> findByCustomerIdAndRestaurantId(Integer customerId, Integer restaurantId);

    Optional<Cart> findByCustomerIdAndStatus(Integer customerId, CartStatus status);

    Optional<Cart> findByCustomerIdAndRestaurantIdAndStatus(Integer customerId, Integer restaurantId, CartStatus cartStatus);

    boolean existsByCustomerIdAndRestaurantIdAndStatus(Integer customerId, Integer restaurantId, CartStatus status);

}
