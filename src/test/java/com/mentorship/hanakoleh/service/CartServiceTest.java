//package com.mentorship.hanakoleh.service;
//
//import com.mentorship.hanakoleh.domain.cart.dto.CartItemDTO;
//import com.mentorship.hanakoleh.domain.cart.model.Cart;
//import com.mentorship.hanakoleh.domain.cart.model.CartItem;
//import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
//import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
//import com.mentorship.hanakoleh.domain.user.Customer;
//import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
//import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
//import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
//import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CartServiceTest {
//
//    @Mock
//    private CartRepository cartRepository;
//
//    @Mock
//    private CartItemRepository cartItemRepository;
//
//    @Mock
//    private MenuService menuService;
//
//    @Mock
//    private CustomerService customerService;
//
//    @Mock
//    private RestaurantService restaurantService;
//
//    @InjectMocks
//    private CartService cartService;
//
//    @Nested
//    @DisplayName("Tests for createCartForCustomer")
//    class CreateCartForCustomerTests {
//
//        @BeforeEach
//        void setUp(){
//            User user =
//            Customer customer = Customer.builder().id(123).user()
//            Restaurant restaurant = new Restaurant();
//            Cart cart = new Cart();
//        }
//        @Test
//        @DisplayName("Should successfully create a new cart")
//        void shouldCreateCartSuccessfully() {
//            Integer customerId = 1;
//            Integer restaurantId = 10;
//
//            Cart cart = Cart.builder().customer(customer).restaurant(restaurant).build();
//
//            when(customerService.getCustomerReferenceById(customerId)).thenReturn(customer);
//            when(restaurantService.getRestaurantReferenceById(restaurantId)).thenReturn(restaurant);
//            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
//
//            Cart result = cartService.createCartForCustomer(customerId, restaurantId);
//
//            assertNotNull(result);
//            verify(customerService).getCustomerReferenceById(customerId);
//            verify(restaurantService).getRestaurantReferenceById(restaurantId);
//            verify(cartRepository).save(any(Cart.class));
//        }
//
//        @Test
//        @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
//        void shouldThrowCustomerNotFoundException() {
//            Integer testCustomerId = 1;
//            Integer testRestaurantId = 123;
//
//            when(customerService.getCustomerReferenceById(testCustomerId))
//                    .thenThrow(new CustomerNotFoundException("Customer not found with ID: " + testCustomerId));
//
//            CustomerNotFoundException exception = assertThrows(
//                    CustomerNotFoundException.class,
//                    () -> cartService.createCartForCustomer(testCustomerId, testRestaurantId)
//            );
//
//            assertEquals("Customer not found with ID: " + testCustomerId, exception.getMessage());
//            verify(customerService, times(1)).getCustomerReferenceById(testCustomerId);
//            verifyNoInteractions(restaurantService);
//            verifyNoInteractions(cartRepository);
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests for findCartByCustomerId")
//    class FindCartByCustomerIdTests {
//
//        @Test
//        @DisplayName("Should return cart when customer cart exists")
//        void shouldReturnCartWhenExists() {
//            Integer customerId = 1;
//            when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
//
//            Cart result = cartService.findCartByCustomerId(customerId);
//
//            assertNotNull(result);
//            verify(cartRepository).findByCustomerId(customerId);
//        }
//
//        @Test
//        @DisplayName("Should throw CartNotFoundException when cart does not exist")
//        void shouldThrowExceptionWhenCartNotFound() {
//            Integer customerId = 1;
//            when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
//
//            CartNotFoundException exception = assertThrows(
//                    CartNotFoundException.class,
//                    () -> cartService.findCartByCustomerId(customerId)
//            );
//
//            assertEquals("Cart not found for Customer " + customerId, exception.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests for clearCart")
//    class ClearCartTests {
//
//        @Test
//        @DisplayName("Should delete all cart items for customer")
//        void shouldClearCartItems() {
//            Integer userId = 5;
//            Integer customerId = 1;
//            Integer cartId = 100;
//
//            Cart cart = Cart.builder().id(cartId).build();
//
//            when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
//            when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
//
//            cartService.clearCart(userId);
//
//            verify(cartItemRepository).deleteByCartId(cartId);
//        }
//    }
//
//    @Nested
//    @DisplayName("Tests for addItemToCart")
//    class AddItemToCartTests {
//
//        @Test
//        @DisplayName("Should add new item to cart when item doesn't exist in cart")
//        void shouldAddNewItemToCart() {
//            Integer userId = 1;
//            Integer customerId = 2;
//            Integer restaurantId = 10;
//            Integer cartId = 100;
//            Integer menuItemId = 50;
//
//            Restaurant restaurant = Restaurant.builder().id(restaurantId).build();
//            Cart cart = Cart.builder().id(cartId).restaurant(restaurant).build();
//
//            MenuItem menuItem = MenuItem.builder()
//                    .id(menuItemId)
//                    .restaurant(restaurant)
//                    .price(BigDecimal.valueOf(15.00))
//                    .build();
//
//            CartItemDTO dto = CartItemDTO.builder()
//                    .selectedMenuItemId(menuItemId)
//                    .quantity(2)
//                    .note("Extra spicy")
//                    .build();
//
//            CartItem savedItem = CartItem.builder()
//                    .menuItem(menuItem)
//                    .cart(cart)
//                    .price(menuItem.getPrice())
//                    .quantity(dto.getQuantity())
//                    .note(dto.getNote())
//                    .build();
//
//            when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
//            when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
//            when(menuService.findMenuItemById(menuItemId)).thenReturn(menuItem);
//            when(cartItemRepository.findByCartIdAndMenuItemId(cartId, menuItemId)).thenReturn(Optional.empty());
//            when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);
//
//            CartItemDTO result = cartService.addItemToCart(dto, userId);
//
//            assertNotNull(result);
//            assertEquals(menuItemId, result.getSelectedMenuItemId());
//            assertEquals(2, result.getQuantity());
//            assertEquals("Extra spicy", result.getNote());
//        }
//
//
//    }
//}