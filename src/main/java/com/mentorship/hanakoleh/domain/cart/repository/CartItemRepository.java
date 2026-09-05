package com.mentorship.hanakoleh.domain.cart.repository;

import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

}
