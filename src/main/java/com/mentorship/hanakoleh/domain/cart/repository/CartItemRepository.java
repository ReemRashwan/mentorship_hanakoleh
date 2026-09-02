package com.mentorship.hanakoleh.domain.cart.repository;

import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    @EntityGraph(attributePaths = "menuItem")
    Optional<CartItem> findWithMenuItemById(Integer id);
}
