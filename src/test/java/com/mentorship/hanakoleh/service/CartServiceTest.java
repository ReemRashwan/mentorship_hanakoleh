package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemRequest;
import com.mentorship.hanakoleh.domain.cart.dto.CartItemDTO;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import com.mentorship.hanakoleh.domain.cart.service.MenuService;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    // 1# identify dependencies
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private RestaurantService restaurantService;

    // 2# private instance of targeted test class
    @InjectMocks
    private CartService cartService;

    // 3# define frequent used inputs
    private Integer userId;
    private Integer restaurantId;
    private Integer customerId;
    private Integer cartId;
    private Integer cartItemId;
    private Integer menuId;
    private Integer menuItemId;
    private Cart cart;
    private CartItem cartItem;
    private Menu menu;
    private MenuItem menuItem;
    private AddCartItemRequest addCartItemRequestDto;


    // 4# create a nested class for each method to organize tests
    @Nested
    @DisplayName("Tests for createCartForCustomer")
    class CreateCartForCustomerTests {

        @BeforeEach
        void setUp(){
            userId = 1;
            customerId = 101;
            restaurantId = 10;
            cartId = 100;
            cartItemId = 50;
            menuId = 100;
            menuItemId = 50;
            Restaurant restaurant = Restaurant.builder().id(restaurantId).build();
            CartServiceTest.this.menu = Menu.builder().build();
            CartServiceTest.this.menuItem = MenuItem.builder()
                    .id(menuItemId)
                    .restaurant(restaurant)
                    .price(BigDecimal.valueOf(15.00))
                    .build();
            CartServiceTest.this.cart = Cart.builder().id(cartId).restaurant(restaurant).build();
            CartServiceTest.this.cartItem = CartItem.builder()
                    .menuItem(menuItem)
                    .cart(cart)
                    .price(menuItem.getPrice())
                    .quantity(dto.getQuantity())
                    .note(dto.getNote())
                    .build();




        }

        @Nested
        @DisplayName("Tests for addItemToCart")
        class AddItemToCartTests {

            @Test
            @DisplayName("Should successfully add new item to cart when item doesn't exist in cart")
            void shouldSuccessfullyAddNewItemToCart() {
                //Given
                addCartItemRequestDto= AddCartItemRequest.builder().build();
                }




                when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
                when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
                when(menuService.findMenuItemById(menuItemId)).thenReturn(menuItem);
                when(cartItemRepository.findByCartIdAndMenuItemId(cartId, menuItemId)).thenReturn(Optional.empty());
                when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);

                CartItemDTO result = cartService.addItemToCart(dto, userId);

                assertNotNull(result);
                assertEquals(menuItemId, result.getSelectedMenuItemId());
                assertEquals(2, result.getQuantity());
                assertEquals("Extra spicy", result.getNote());
            }


        }
/*        @Test
        @DisplayName("Should successfully create a new cart")
        void shouldCreateCartSuccessfully() {
            Integer customerId = 1;
            Integer restaurantId = 10;

            Cart cart = Cart.builder().customer(customer).restaurant(restaurant).build();

            when(customerService.getCustomerReferenceById(customerId)).thenReturn(customer);
            when(restaurantService.getRestaurantReferenceById(restaurantId)).thenReturn(restaurant);
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            Cart result = cartService.createCartForCustomer(customerId, restaurantId);

            assertNotNull(result);
            verify(customerService).getCustomerReferenceById(customerId);
            verify(restaurantService).getRestaurantReferenceById(restaurantId);
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
        void shouldThrowCustomerNotFoundException() {
            Integer testCustomerId = 1;
            Integer testRestaurantId = 123;

            when(customerService.getCustomerReferenceById(testCustomerId))
                    .thenThrow(new CustomerNotFoundException("Customer not found with ID: " + testCustomerId));

            CustomerNotFoundException exception = assertThrows(
                    CustomerNotFoundException.class,
                    () -> cartService.createCartForCustomer(testCustomerId, testRestaurantId)
            );

            assertEquals("Customer not found with ID: " + testCustomerId, exception.getMessage());
            verify(customerService, times(1)).getCustomerReferenceById(testCustomerId);
            verifyNoInteractions(restaurantService);
            verifyNoInteractions(cartRepository);
        }
    }

    @Nested
    @DisplayName("Tests for findCartByCustomerId")
    class FindCartByCustomerIdTests {

        @Test
        @DisplayName("Should return cart when customer cart exists")
        void shouldReturnCartWhenExists() {
            Integer customerId = 1;
            when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

            Cart result = cartService.findCartByCustomerId(customerId);

            assertNotNull(result);
            verify(cartRepository).findByCustomerId(customerId);
        }

        @Test
        @DisplayName("Should throw CartNotFoundException when cart does not exist")
        void shouldThrowExceptionWhenCartNotFound() {
            Integer customerId = 1;
            when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

            CartNotFoundException exception = assertThrows(
                    CartNotFoundException.class,
                    () -> cartService.findCartByCustomerId(customerId)
            );

            assertEquals("Cart not found for Customer " + customerId, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for clearCart")
    class ClearCartTests {

        @Test
        @DisplayName("Should delete all cart items for customer")
        void shouldClearCartItems() {
            Integer userId = 5;
            Integer customerId = 1;
            Integer cartId = 100;

            Cart cart = Cart.builder().id(cartId).build();

            when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
            when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

            cartService.clearCart(userId);

            verify(cartItemRepository).deleteByCartId(cartId);
        }
    }*/


}