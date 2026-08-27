package com.mentorship.hanakoleh.domain.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.MenuItemNotOrderableException;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private static final Integer CART_ITEM_ID = 1;
    private static final Integer MENU_ITEM_ID = 7;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void shouldUpdateQuantityWhenMenuItemCanSupplyIt() {
        CartItem cartItem = cartItemWithQuantity(2, menuItem(10, MenuItemOnDemandStatus.AVAILABLE));
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        UpdateCartItemQuantityResponse response = cartService.updateItemQuantity(CART_ITEM_ID, 3);

        assertEquals(3, response.quantity().intValue());
        assertEquals(CART_ITEM_ID, response.cartItemId());
        assertEquals(MENU_ITEM_ID, response.menuItemId());
        assertEquals(3, cartItem.getQuantity().intValue());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void shouldAllowDecreaseEvenWhenMenuItemIsOutOfStock() {
        CartItem cartItem = cartItemWithQuantity(5, menuItem(0, MenuItemOnDemandStatus.OUT_OF_STOCK));
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        UpdateCartItemQuantityResponse response = cartService.updateItemQuantity(CART_ITEM_ID, 2);

        assertEquals(2, response.quantity().intValue());
    }

    @Test
    void shouldRejectQuantityBelowOneWithoutTouchingTheDatabase() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.updateItemQuantity(CART_ITEM_ID, 0));

        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void shouldRejectNullQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.updateItemQuantity(CART_ITEM_ID, null));

        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void shouldFailWhenCartItemDoesNotExist() {
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class,
                () -> cartService.updateItemQuantity(CART_ITEM_ID, 3));

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void shouldFailWhenIncreaseExceedsAvailableQuantity() {
        CartItem cartItem = cartItemWithQuantity(2, menuItem(10, MenuItemOnDemandStatus.AVAILABLE));
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));

        assertThrows(MenuItemNotOrderableException.class,
                () -> cartService.updateItemQuantity(CART_ITEM_ID, 20));

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void shouldFailWhenIncreasingAnUnavailableMenuItem() {
        CartItem cartItem = cartItemWithQuantity(2, menuItem(50, MenuItemOnDemandStatus.UNAVAILABLE));
        when(cartItemRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cartItem));

        assertThrows(MenuItemNotOrderableException.class,
                () -> cartService.updateItemQuantity(CART_ITEM_ID, 3));

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    private CartItem cartItemWithQuantity(Integer quantity, MenuItem menuItem) {
        return CartItem.builder()
                .id(CART_ITEM_ID)
                .menuItem(menuItem)
                .price(new BigDecimal("55.00"))
                .quantity(quantity)
                .note("no onions")
                .build();
    }

    private MenuItem menuItem(Integer availableQuantity, MenuItemOnDemandStatus status) {
        return MenuItem.builder()
                .id(MENU_ITEM_ID)
                .name("Koshary Large")
                .price(new BigDecimal("55.00"))
                .availableQuantity(availableQuantity)
                .onDemandStatus(status)
                .build();
    }
}
