package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemRequest;
import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemResponse;
import com.mentorship.hanakoleh.domain.cart.dto.ClearCartResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.cart.service.CartService;
import com.mentorship.hanakoleh.domain.restaurant.exception.CrossRestaurantConflictException;
import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemOutOfStock;
import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemUnavailableException;
import com.mentorship.hanakoleh.domain.restaurant.model.*;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import com.mentorship.hanakoleh.domain.user.model.*;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private CartService cartService;

    @Nested
    @DisplayName("Tests for Add Item to cart")
    class AddItemToCartTests {

        private Integer userId;
        private Integer restaurantId;
        private Integer customerId;
        private Integer cartId;
        private Integer cartItemId;
        private Integer menuId;
        private Integer menuItemId;

        private User mockUser;
        private Customer mockCustomer;
        private Restaurant mockRestaurant;
        private ItemCategory mockItemCategory;
        private Cart mockCart;
        private CartItem mockCartItem;
        private Menu mockMenu;
        private MenuItem mockMenuItem;
        private AddCartItemRequest addCartItemRequest;

        @BeforeEach
        void setUp() {
            userId = 101;
            customerId = 101;
            restaurantId = 10;
            cartId = 10;
            cartItemId = 110;
            menuId = 10;
            menuItemId = 110;

            mockUser = User.builder().id(userId).userType(UserType.builder().id(1).name("Customer").description("Customer who purchase food online").build()).email("zeinab@google.om").phoneNumber("091091").firstName("Zeinab").lastName("Osman").language(Language.builder().id(1).name("Arabic").code("AR").build()).passwordHash("112233").joinedAt(OffsetDateTime.now()).lastLoginAt(OffsetDateTime.now().minusDays(4)).lastLoginStatus(LoginStatus.SUCCESS).build();
            mockCustomer = Customer.builder().id(customerId).user(mockUser).notificationStatus(true).build();
            mockRestaurant = Restaurant.builder().id(restaurantId).name("GrillOnWheels").phone("123456").rating(BigDecimal.valueOf(1000)).longitude(BigDecimal.valueOf(15.577968879582503)).latitude(BigDecimal.valueOf(32.5685861095599)).avgPreparationTimeInMins(30).createdAt(OffsetDateTime.now()).build();
            mockMenu = Menu.builder().id(menuId).restaurant(mockRestaurant).name("Meat Burgers").uiOrder(1).visible(true).build();
            mockItemCategory = ItemCategory.builder().id(menuItemId).name("Burgers").build();
            mockMenuItem = MenuItem.builder().id(menuItemId).menu(mockMenu).category(mockItemCategory)
                    .name("Juicy Lucy Double Cheese Burger")
                    .price(BigDecimal.valueOf(15.99))
                    .availableQuantity(5)
                    .uiOrder(1)
                    .onDemandStatus(MenuItemOnDemandStatus.AVAILABLE)
                    .build();

            mockCart = Cart.builder()
                    .id(cartId)
                    .customer(mockCustomer)
                    .restaurant(mockRestaurant)
                    .status(CartStatus.ACTIVE)
                    .createdAt(OffsetDateTime.now().minusWeeks(2).toInstant())
                    .items(new ArrayList<>())
                    .build();
        }

        @Test
        @DisplayName("Should successfully add new item to cart when item doesn't exist in cart")
        void shouldSuccessfullyAddNewItemToCart_WhenCartIsAvailableAndItemDoesNotExistInCart() {
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, 2, "No onions");

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(10);

            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            assertNotNull(response);
            assertEquals(CartStatus.ACTIVE, response.status());
            assertEquals(restaurantId, response.restaurantId());

            assertEquals(1, mockCart.getItems().size());
            CartItem addedItem = mockCart.getItems().get(0);
            assertEquals(menuItemId, addedItem.getMenuItem().getId());
            assertEquals(2, addedItem.getQuantity());
            assertEquals(BigDecimal.valueOf(15.99), addedItem.getPrice());
            assertEquals("No onions", addedItem.getNote());
        }

        @Test
        @DisplayName("Should successfully add new item to cart when item already exists in cart")
        void shouldSuccessfullyAddNewItemToCart_WhenCartIsAvailableAndItemExistsInCart() {
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, 2, "No onions");
            mockCartItem = CartItem.builder().id(cartItemId).cart(mockCart).menuItem(mockMenuItem).price(BigDecimal.valueOf(15.99)).quantity(2).note("Not spicy").build();
            mockCart.getItems().add(mockCartItem);

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(5);

            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            assertNotNull(response);
            assertEquals(CartStatus.ACTIVE, response.status());
            assertEquals(restaurantId, response.restaurantId());

            assertEquals(1, mockCart.getItems().size());
            CartItem addedItem = mockCart.getItems().get(0);
            assertEquals(menuItemId, addedItem.getMenuItem().getId());
            assertEquals(4, addedItem.getQuantity());
            assertEquals(BigDecimal.valueOf(15.99), addedItem.getPrice());
            assertEquals("No onions", addedItem.getNote());
        }

        @Test
        @DisplayName("Should successfully add new item to cart when a different item already exists in cart")
        void shouldSuccessfullyAddNewItemToCart_WhenCartIsAvailableAndDifferentItemExistsInCart() {
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, 2, "No onions");
            MenuItem differentMenuItem = MenuItem.builder().id(99).build();

            CartItem existingCartItem = CartItem.builder()
                    .id(120)
                    .cart(mockCart)
                    .menuItem(differentMenuItem)
                    .price(BigDecimal.valueOf(20))
                    .quantity(6)
                    .note("Not spicy")
                    .build();

            mockCart.getItems().add(existingCartItem);

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(5);

            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            assertNotNull(response);
            assertEquals(CartStatus.ACTIVE, response.status());
            assertEquals(restaurantId, response.restaurantId());

            assertEquals(2, mockCart.getItems().size());

            CartItem firstAddedItem = mockCart.getItems().get(0);
            assertEquals(99, firstAddedItem.getMenuItem().getId());
            assertEquals(6, firstAddedItem.getQuantity());
            assertEquals(BigDecimal.valueOf(20), firstAddedItem.getPrice());
            assertEquals("Not spicy", firstAddedItem.getNote());

            CartItem secAddedItem = mockCart.getItems().get(1);
            assertEquals(menuItemId, secAddedItem.getMenuItem().getId());
            assertEquals(2, secAddedItem.getQuantity());
            assertEquals(BigDecimal.valueOf(15.99), secAddedItem.getPrice());
            assertEquals("No onions", secAddedItem.getNote());
        }

        @Test
        @DisplayName("Should successfully create an active cart and add the new item when customer has no active cart")
        void shouldSuccessfullyAddNewItem_WhenCustomerHasNoActiveCart() {
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, 4, "No onions");

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(customerService.getCustomerReferenceById(anyInt())).thenReturn(mockCustomer);
            when(restaurantService.getRestaurantReferenceById(restaurantId)).thenReturn(mockRestaurant);
            when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                    .thenReturn(Optional.empty());
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(5);
            when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            assertNotNull(response);
            assertEquals(CartStatus.ACTIVE, response.status());
            assertEquals(restaurantId, response.restaurantId());

            ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
            verify(cartRepository).save(cartCaptor.capture());

            Cart capturedCart = cartCaptor.getValue();
            assertNotNull(capturedCart);
            assertEquals(0, capturedCart.getItems().size());

            CartItem addedItem = mockCart.getItems().get(0);
            assertEquals(menuItemId, addedItem.getMenuItem().getId());
            assertEquals(4, addedItem.getQuantity());
            assertEquals(BigDecimal.valueOf(15.99), addedItem.getPrice());
            assertEquals("No onions", addedItem.getNote());
        }

        @Test
        @DisplayName("Should throw exception if the item requested to be added is from a different restaurant")
        void shouldThrowCrossRestaurantConflictException_WhenItemIsFromDifferentRestaurant() {
            CartItem cartItemFromDifferentRestaurant = CartItem.builder().id(999).menuItem(MenuItem.builder().id(999).build()).build();
            mockCart.getItems().add(cartItemFromDifferentRestaurant);
            addCartItemRequest = new AddCartItemRequest(menuItemId, 20, 2, "No onions");

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                    .thenReturn(Optional.of(mockCart));

            final CrossRestaurantConflictException exception = assertThrows(
                    CrossRestaurantConflictException.class,
                    () -> cartService.addItemToCart(addCartItemRequest, userId)
            );
            assertEquals(String.format("Active cart exists for restaurant ID %d, but requested item belong to restaurant ID %d", restaurantId, 20), exception.getMessage());
            verify(customerService, times(1)).retrieveCustomerIdByUserId(userId);
            verify(restaurantService, times(1)).getMenuItemByMenuItemId(anyInt());
        }

        @Test
        @DisplayName("Should throw exception when selected menu item does not exist")
        void shouldThrowMenuItemUnavailableExceptionException_WhenMenuItemDoesNotExist() {
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, 2, "No onions");

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.empty());

            MenuItemUnavailableException exception = assertThrows(
                    MenuItemUnavailableException.class,
                    () -> cartService.addItemToCart(addCartItemRequest, userId)
            );

            assertEquals("Menu Item with ID: " + menuItemId + " is not available.", exception.getMessage());
            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("Should throw exception when menu item inventory is insufficient")
        void shouldThrowMenuItemOutOfStock_WhenMenuItemIsOutOfStock() {
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, 5, "No onions");

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(2);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));

            MenuItemOutOfStock exception = assertThrows(
                    MenuItemOutOfStock.class,
                    () -> cartService.addItemToCart(addCartItemRequest, userId)
            );

            assertEquals("Menu Item with ID: " + menuItemId + " is out of Stock.", exception.getMessage());
            verify(cartRepository, never()).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Tests for Clear Cart")
    class ClearCartTests {

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
            when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                    .thenReturn(Optional.of(activeCart));

            ClearCartResponse response = cartService.clearCart(userId);

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
            activeCart.setRestaurant(null);

            when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                    .thenReturn(Optional.of(activeCart));

            ClearCartResponse response = cartService.clearCart(userId);

            assertThat(response).isNotNull();
            assertThat(response.getCartId()).isEqualTo(cartId);
            assertThat(response.getCustomerId()).isEqualTo(customerId);
            assertThat(response.getRestaurantId()).isNull();
            assertThat(activeCart.getItems()).isEmpty();
        }

        @Test
        @DisplayName("Should throw CartNotFoundException when no active cart exists for customer")
        void clearCart_ThrowsException_WhenNoActiveCartFound() {
            when(customerService.retrieveCustomerIdByUserId(userId)).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.clearCart(userId))
                    .isInstanceOf(CartNotFoundException.class)
                    .hasMessage("No Active Cart found for Customer Id " + customerId);

            verify(customerService).retrieveCustomerIdByUserId(userId);
            verify(cartRepository).findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);
        }
    }
}