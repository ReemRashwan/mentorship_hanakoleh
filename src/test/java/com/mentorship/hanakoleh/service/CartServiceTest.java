package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemRequest;
import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemResponse;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTests {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private CartService cartService;

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

    @Nested
    @DisplayName("Tests for Add Item to cart.")
    class AddItemToCartTests {

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
            // Given
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, BigDecimal.valueOf(18), 2, "No onions");

            // When
            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(10);

            // Act
            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            // Assert
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
            // Given
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, BigDecimal.valueOf(15.99), 2, "No onions");
            mockCartItem = CartItem.builder().id(cartItemId).cart(mockCart).menuItem(mockMenuItem).price(BigDecimal.valueOf(15.99)).quantity(2).note("Not spicy").build();
            mockCart.getItems().add(mockCartItem);

            // When
            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(5);

            // Act
            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            // Assert
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
            // Given
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, BigDecimal.valueOf(15.99), 2, "No onions");
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

            // When
            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(5);

            // Act
            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            // Assert
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
        @DisplayName("Should successfully create an active cart and add the new item when customer has no active cart ")
        void shouldSuccessfullyAddNewItem_WhenCustomerHasNoActiveCart() {
            // Given
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, BigDecimal.valueOf(15.99), 4, "No onions");

            // When
            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(customerService.getCustomerReferenceById(anyInt())).thenReturn(mockCustomer);
            when(restaurantService.getRestaurantReferenceById(restaurantId)).thenReturn(mockRestaurant);
            when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                    .thenReturn(Optional.empty());
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(5);
            when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

            // Act
            AddCartItemResponse response = cartService.addItemToCart(addCartItemRequest, userId);

            // Assert
            assertNotNull(response);
            assertEquals(CartStatus.ACTIVE, response.status());
            assertEquals(restaurantId, response.restaurantId());

            ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
            verify(cartRepository).save(cartCaptor.capture());

            Cart capturedCart = cartCaptor.getValue();
            assertNotNull(capturedCart);
            assertEquals(0, capturedCart.getItems().size());

            // Switched to evaluating mockCart items for consistency with other tests
            CartItem addedItem = mockCart.getItems().get(0);
            assertEquals(menuItemId, addedItem.getMenuItem().getId());
            assertEquals(4, addedItem.getQuantity());
            assertEquals(BigDecimal.valueOf(15.99), addedItem.getPrice());
            assertEquals("No onions", addedItem.getNote());
        }

        @Test
        @DisplayName("Should throw exception if the item requested to be added is from a different restaurant")
        void shouldThrowCrossRestaurantConflictException_WhenItemIsFromDifferentRestaurant() {
            // Given
            CartItem cartItemFromDifferentRestaurant = CartItem.builder().id(999).menuItem(MenuItem.builder().id(999).build()).build();
            mockCart.getItems().add(cartItemFromDifferentRestaurant);
            addCartItemRequest = new AddCartItemRequest(menuItemId, 20, BigDecimal.valueOf(18), 2, "No onions");

            // When
            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE))
                    .thenReturn(Optional.of(mockCart));

            // Act & Assert
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
            // Given
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, BigDecimal.valueOf(15.99), 2, "No onions");

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.empty());

            // Act & Assert
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
            // Given
            addCartItemRequest = new AddCartItemRequest(menuItemId, restaurantId, BigDecimal.valueOf(15.99), 5, "No onions");

            when(customerService.retrieveCustomerIdByUserId(anyInt())).thenReturn(customerId);
            when(restaurantService.getMenuItemByMenuItemId(menuItemId))
                    .thenReturn(Optional.of(mockMenuItem));
            when(restaurantService.getMenuItemInventory(menuItemId)).thenReturn(2);
            when(cartRepository.findByCustomerIdAndStatus(anyInt(), eq(CartStatus.ACTIVE)))
                    .thenReturn(Optional.of(mockCart));

            // Act & Assert
            MenuItemOutOfStock exception = assertThrows(
                    MenuItemOutOfStock.class,
                    () -> cartService.addItemToCart(addCartItemRequest, userId)
            );

            assertEquals("Menu Item with ID: " + menuItemId + " is out of Stock.", exception.getMessage());
            verify(cartRepository, never()).save(any(Cart.class));
        }
    }
}