package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.controller.DTO.CartItemDTO;
import com.mentorship.hanakoleh.domain.cart.Cart;
import com.mentorship.hanakoleh.domain.cart.CartItem;
import com.mentorship.hanakoleh.domain.restaurant.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.Restaurant;
import com.mentorship.hanakoleh.domain.user.Customer;
import com.mentorship.hanakoleh.exception.CartNotFoundException;
import com.mentorship.hanakoleh.repository.CartItemRepository;
import com.mentorship.hanakoleh.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuService menuService;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;

    @Transactional
    public Cart createCartForCustomer(Integer customerId, Integer restaurantId) {
        Customer customer = customerService.getCustomerReferenceById(customerId);
        Restaurant restaurant = restaurantService.getRestaurantReferenceById(restaurantId);

        Cart cart = Cart.builder()
                .createdAt(OffsetDateTime.now())
                .restaurant(restaurant)
                .customer(customer)
                .build();

        return cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Cart findCartByCustomerId(Integer customerId) {
        return cartRepository.findByCustomerId(customerId).orElseThrow(()->new CartNotFoundException("Cart not found"));

    }

    @Transactional(readOnly = true)
    public Cart checkOrAssignCart(Integer customerId,Integer restaurantId) {
        return cartRepository.findByCustomerId(customerId).orElse(createCartForCustomer(customerId, restaurantId));

    }

    @Transactional(readOnly = true)
    public Optional<Cart> findById(Integer id) {
        return cartRepository.findById(id);
    }

    @Transactional
    public void removeCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Integer userId) {
        Integer customerId = customerService.retrieveCustomerIdByUserId(userId);
        Cart customerCart = findCartByCustomerId(customerId);
        cartItemRepository.deleteByCartId(customerCart.getId());
    }

    @Transactional
    public CartItemDTO addItemToCart(CartItemDTO cartItemDTO, Integer userId) {
        Integer customerId = customerService.retrieveCustomerIdByUserId(userId);
        MenuItem selectedMenuItem = menuService.findMenuItemById(cartItemDTO.getSelectedMenuItemId());
        //ensure customer has a cart and if not assign one
        Cart customerCart = checkOrAssignCart(customerId,selectedMenuItem.getMenu().getRestaurant().getId());

        // Ensure the item belongs to the restaurant associated with the active cart
        if (!selectedMenuItem.getMenu().getRestaurant().getId().equals(customerCart.getRestaurant().getId())) {
            throw new IllegalArgumentException("Cannot add items from a different restaurant to this cart");
        }

        // Functional create for the cart item
        CartItem cartItem = cartItemRepository
                .findByCartIdAndMenuItemId(customerCart.getId(), selectedMenuItem.getId())
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
                    if (cartItemDTO.getNote() != null) {
                        existingItem.setNote(cartItemDTO.getNote());
                    }
                    return existingItem;
                })
                .orElseGet(() -> CartItem.builder()
                        .menuItem(selectedMenuItem)
                        .cart(customerCart)
                        .price(selectedMenuItem.getPrice())
                        .quantity(cartItemDTO.getQuantity())
                        .note(cartItemDTO.getNote())
                        .build());

        return mapToCartItemDTO(cartItemRepository.save(cartItem));
    }

    private CartItemDTO mapToCartItemDTO(CartItem cartItem) {
        return CartItemDTO.builder()
                .selectedMenuItemId(cartItem.getMenuItem().getId())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .note(cartItem.getNote())
                .build();
    }
}