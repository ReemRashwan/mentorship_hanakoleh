package com.mentorship.hanakoleh.domain.cart.service;

import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.MenuItemNotOrderableException;
import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public UpdateCartItemQuantityResponse updateItemQuantity(Integer cartItemId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0, or delete the item from the cart.");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        if (quantity > cartItem.getQuantity()) {
            validateMenuItemCanSupply(cartItem.getMenuItem(), quantity);
        }

        cartItem.setQuantity(quantity);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        return toResponse(updatedCartItem);
    }

    private void validateMenuItemCanSupply(MenuItem menuItem, Integer quantity) {
        MenuItemOnDemandStatus status = menuItem.getOnDemandStatus();
        if (status == MenuItemOnDemandStatus.UNAVAILABLE || status == MenuItemOnDemandStatus.OUT_OF_STOCK) {
            throw new MenuItemNotOrderableException(
                    "Menu item " + menuItem.getId() + " is currently " + status + ".");
        }

        Integer availableQuantity = menuItem.getAvailableQuantity();
        if (availableQuantity != null && quantity > availableQuantity) {
            throw new MenuItemNotOrderableException(
                    "Only " + availableQuantity + " units of menu item " + menuItem.getId() + " are available.");
        }
    }

    private UpdateCartItemQuantityResponse toResponse(CartItem cartItem) {
        return new UpdateCartItemQuantityResponse(
                cartItem.getId(),
                cartItem.getMenuItem().getId(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                cartItem.getNote());
    }
}
