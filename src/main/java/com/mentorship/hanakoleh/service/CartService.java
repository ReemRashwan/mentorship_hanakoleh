package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.controller.DTO.CartItemDTO;
import com.mentorship.hanakoleh.domain.cart.Cart;
import com.mentorship.hanakoleh.domain.cart.CartItem;
import com.mentorship.hanakoleh.domain.restaurant.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.Restaurant;
import com.mentorship.hanakoleh.domain.user.Customer;
import com.mentorship.hanakoleh.exceptions.CartNotFoundException;
import com.mentorship.hanakoleh.repository.CartItemRepository;
import com.mentorship.hanakoleh.repository.CartRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final MenuService menuService;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;

    @Autowired
    public CartService(CartRepository cartRepository, UserService userService, MenuService menuService, CustomerService customerService, RestaurantService restaurantService, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.menuService = menuService;
        this.customerService = customerService;
        this.restaurantService = restaurantService;
    }

    public Cart createCartForCustomer(Integer customerId, Integer restaurantID) {
        Customer customer = customerService.getCustomerReferenceById(customerId);
        Restaurant restaurant = restaurantService.getRestaurantReferenceById(restaurantID);
        Cart cart = Cart.builder()
                .createdAt(OffsetDateTime.now()).restaurant(restaurant).customer(customer).build();
        return cartRepository.save(cart);
    }

    public Cart findCartByCustomerId(Integer customerId) {
        return cartRepository.findByCustomerId(customerId).orElseThrow(() -> new CartNotFoundException("Cart not found for Customer" + customerId));
    }

    public Optional<Cart> findById(Integer id) {

        return cartRepository.findById(id);
    }

    public void removeCart(Integer cartId) {

        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCart(Integer userId) {
        Integer customerId = customerService.retrieveCustomerIdByUserId(userId);
        Cart customerCart = findCartByCustomerId(customerId);
        cartItemRepository.deleteByCartId(customerCart.getId());
    }

    public CartItemDTO addItemToCart( CartItemDTO cartItemDTO, Integer userId) {
        Integer customerId = customerService.retrieveCustomerIdByUserId(userId);
        Cart customerCart = findCartByCustomerId(customerId);
        MenuItem selectedMenuItem = menuService.findMenuItemById(cartItemDTO.getSelectedMenuItemId());

        // Check if item is already in the cart
        Optional<CartItem> existingCartItem = cartItemRepository
                .findByCartIdAndMenuItemId(customerCart.getId(), selectedMenuItem.getId());

        CartItem cartItem;

        if (existingCartItem.isPresent()) {
            // Update quantity of existing item
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
            if (cartItemDTO.getNote() != null) {
                cartItem.setNote(cartItemDTO.getNote());
            }
        } else {
            // Create new cart item
            cartItem = CartItem.builder()
                    .menuItem(selectedMenuItem)
                    .cart(customerCart)
                    .price(selectedMenuItem.getPrice())
                    .quantity(cartItemDTO.getQuantity())
                    .note(cartItemDTO.getNote())
                    .build();
        }

        // Save and return persisted entity
        CartItemDTO addedCartItem=  mapToCartItemDTO(cartItemRepository.save(cartItem));
        BigDecimal subtotal = selectedMenuItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        //customerCart.updateTotalPrice(subtotal); To be discussed further
        return addedCartItem;

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



