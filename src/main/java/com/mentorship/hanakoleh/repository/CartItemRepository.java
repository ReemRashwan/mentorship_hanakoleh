package com.mentorship.hanakoleh.repository;

import com.mentorship.hanakoleh.domain.cart.Cart;
import com.mentorship.hanakoleh.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    public Optional<CartItem> findByCartIdAndMenuItemId(Integer cart_id, Integer menuItem_id);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    public void deleteByCartId(@Param("cartId") Integer cartId);

}
