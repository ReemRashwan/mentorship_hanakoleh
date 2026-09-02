package com.mentorship.hanakoleh.domain.cart.service;

import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.MenuItemNotOrderableException;
import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;

    public CartService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public CartItem updateItemQuantity(Integer cartItemId, Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException(ErrorCode.QUANTITY_REQUIRED.getMessage());
        }
        if (quantity < 1) {
            throw new IllegalArgumentException(ErrorCode.QUANTITY_MUST_BE_POSITIVE.getMessage());
        }

        CartItem cartItem = cartItemRepository.findWithMenuItemById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        if (quantity > cartItem.getQuantity()) {
            validateMenuItemCanSupply(cartItem.getMenuItem(), quantity);
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    private void validateMenuItemCanSupply(MenuItem menuItem, Integer quantity) {
        MenuItemOnDemandStatus status = menuItem.getOnDemandStatus();
        if (status == MenuItemOnDemandStatus.UNAVAILABLE || status == MenuItemOnDemandStatus.OUT_OF_STOCK) {
            throw new MenuItemNotOrderableException(
                    ErrorCode.MENU_ITEM_NOT_ORDERABLE.format(menuItem.getId(), status));
        }

        Integer availableQuantity = menuItem.getAvailableQuantity();
        if (availableQuantity != null && quantity > availableQuantity) {
            throw new MenuItemNotOrderableException(
                    ErrorCode.MENU_ITEM_INSUFFICIENT_STOCK.format(availableQuantity, menuItem.getId()));
        }
    }
}
