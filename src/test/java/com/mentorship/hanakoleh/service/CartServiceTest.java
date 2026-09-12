package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.domain.cart.dto.ClearCartResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import com.mentorship.hanakoleh.domain.restaurant.model.*;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private final Integer userId = 100;
    private final Integer customerId = 50;
    private final Integer cartId = 10;
    private final Integer restaurantId = 5;
    private final Integer menuId = 1;
    private final Integer menuItemId = 10;
    private final Integer cartItemId1 = 10;
    private final Integer cartItemId2 = 10;

    private Customer customer;
    private Restaurant restaurant;
    private Menu mockMenu;
    private ItemCategory mockItemCategory;
    private Cart activeCart;
    private CartItem mockCartItem1;
    private CartItem mockCartItem2;
    private MenuItem mockMenuItem;


    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(customerId)
                .build();

        restaurant = Restaurant.builder()
                .id(restaurantId)
                .build();



        mockMenu = Menu.builder().id(menuId).restaurant(restaurant).name("Meat Burgers").uiOrder(1).visible(true).build();
        mockItemCategory = ItemCategory.builder().id(menuItemId).name("Burgers").build();
        mockMenuItem = MenuItem.builder().id(menuItemId).menu(mockMenu).category(mockItemCategory)
                .name("Juicy Lucy Double Cheese Burger")
                .price(BigDecimal.valueOf(15.99))
                .availableQuantity(5)
                .uiOrder(1)
                .onDemandStatus(MenuItemOnDemandStatus.AVAILABLE)
                .build();
        mockCartItem1 = CartItem.builder().id(cartItemId1).cart(activeCart).menuItem(mockMenuItem).price(BigDecimal.valueOf(15.99)).quantity(2).note("Not spicy").build();
        mockCartItem2 = CartItem.builder().id(cartItemId2).cart(activeCart).menuItem(mockMenuItem).price(BigDecimal.valueOf(15.99)).quantity(2).note("No Onion").build();
        List<CartItem> items = new ArrayList<>();
        items.add(mockCartItem1);
        items.add(mockCartItem2);
        activeCart = Cart.builder()
                .id(cartId)
                .customer(customer)
                .restaurant(restaurant)
                .status(CartStatus.ACTIVE)
                .items(items)
                .build();
    }

    @Test
    @DisplayName("Should clear cart items successfully when active cart exists with restaurant")
    void clearCart_Success_WithRestaurant() {
        // Given
        when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
        when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(activeCart));

        // When
        ClearCartResponse response = cartService.clearCart(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(cartId);
        assertThat(response.getCustomerId()).isEqualTo(customerId);
        assertThat(response.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(activeCart.getItems()).isEmpty();

        verify(customerService).retrieveCustomerIdByUserId(userId);
        verify(cartRepository).findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should clear cart items successfully when active cart has no restaurant attached")
    void clearCart_Success_NullRestaurant() {
        // Given
        activeCart.setRestaurant(null);

        when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
        when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.of(activeCart));

        // When
        ClearCartResponse response = cartService.clearCart(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(cartId);
        assertThat(response.getCustomerId()).isEqualTo(customerId);
        assertThat(response.getRestaurantId()).isNull();
        assertThat(activeCart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when no active cart exists for customer")
    void clearCart_ThrowsException_WhenNoActiveCartFound() {
        // Given
        when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
        when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cartService.clearCart(userId))
                .isInstanceOf(CartNotFoundException.class)
                .hasMessage("No Active Cart found for Customer Id " + customerId);

        verify(customerService).retrieveCustomerIdByUserId(userId);
        verify(cartRepository).findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);
    }
}