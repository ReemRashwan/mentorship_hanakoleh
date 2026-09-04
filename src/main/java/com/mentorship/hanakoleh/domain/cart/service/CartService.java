package com.mentorship.hanakoleh.domain.cart.service;

import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.MenuItemNotOrderableException;
import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.exception.ErrorCode;
import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.OperationNotAllowedException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import java.util.Objects;
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

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        if (quantity > cartItem.getQuantity()) {
            validateMenuItemCanSupply(cartItem.getMenuItem(), quantity);
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
       
    }

    @Transactional
    public Cart removeCartItem(Integer cartId, Integer itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + cartId));

        if (!CartStatus.ACTIVE.equals(cart.getStatus())) {
            throw new OperationNotAllowedException(String.format("Remove item is not allowed while cart is not active." +
                    " Current cart status: %s", cart.getStatus()));
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        cart.getItems().remove(cartItem);

        if (cart.getItems().isEmpty()) {
            cart.setStatus(CartStatus.EMPTY);
            cart.setRestaurant(null);
        } else {
            cart.setStatus(CartStatus.ACTIVE);
        }

        return cartRepository.save(cart);
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
