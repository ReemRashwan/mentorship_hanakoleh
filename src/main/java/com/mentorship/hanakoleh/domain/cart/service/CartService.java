package com.mentorship.hanakoleh.domain.cart.service;


import com.mentorship.hanakoleh.domain.cart.dto.ClearCartResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.OperationNotAllowedException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.restaurant.validation.MenuItemOrderabilityValidator;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import com.mentorship.hanakoleh.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerService customerService;
    private final MenuItemOrderabilityValidator menuItemOrderabilityValidator;

    @Transactional
    public ClearCartResponse clearCart(Integer userId) {
        Integer customerId = customerService.retrieveCustomerIdByUserId(userId);
        Cart customerCart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new CartNotFoundException("No Active Cart found for Customer Id " + customerId));
        customerCart.getItems().clear();
        return ClearCartResponse.builder()
                .cartId(customerCart.getId())
                .customerId(customerCart.getCustomer().getId())
                .restaurantId(customerCart.getRestaurant() != null ? customerCart.getRestaurant().getId() : null)
                .build();
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
            menuItemOrderabilityValidator.validateOrderable(cartItem.getMenuItem(), quantity);
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

}
