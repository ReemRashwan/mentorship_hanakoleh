package com.mentorship.hanakoleh.domain.cart.service;

import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemRequest;
import com.mentorship.hanakoleh.domain.cart.dto.AddCartItemResponse;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.MenuItemNotOrderableException;
import com.mentorship.hanakoleh.domain.cart.repository.CartItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.exception.ErrorCode;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.OperationNotAllowedException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.restaurant.exception.CrossRestaurantConflictException;
import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemOutOfStock;
import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemUnavailableException;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;

    @Transactional
    public AddCartItemResponse addItemToCart(@NonNull AddCartItemRequest addCartItemRequestDto, @NonNull Integer userId) {
        // 1. Retrieve customer ID from user ID
        Integer customerId = customerService.retrieveCustomerIdByUserId(userId);
        // 2. Retrieve selected menu item
        MenuItem selectedMenuItem = restaurantService.getMenuItemByMenuItemId(addCartItemRequestDto.selectedMenuItemId())
                .orElseThrow(() -> new MenuItemUnavailableException(
                        "Menu Item with ID: " + addCartItemRequestDto.selectedMenuItemId() + " is not available."));

        // 3. Fetch existing or create new active cart for the restaurant
        Cart customerCart = getOrCreateActiveCart(customerId, addCartItemRequestDto.restaurantId());
        // 4. Search for an existing item in cart
        Optional<CartItem> cartItemOptional = customerCart.getItems().stream()
                .filter(x -> x.getMenuItem().equals(selectedMenuItem))
                .findFirst();

        if (cartItemOptional.isPresent()) {
            // 5a. first route if cart item already exist; increase quantity of existing item in the cart
            CartItem cartItem = cartItemOptional.get();
            increaseExistingCartItemQuantity(cartItem, selectedMenuItem, addCartItemRequestDto.quantity());
            if (addCartItemRequestDto.note() != null) {
                cartItem.setNote(addCartItemRequestDto.note());
            }
    public CartItem updateItemQuantity(Integer cartItemId, Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException(ErrorCode.QUANTITY_REQUIRED.getMessage());
        }
        if (quantity < 1) {
            throw new IllegalArgumentException(ErrorCode.QUANTITY_MUST_BE_POSITIVE.getMessage());
        }

        } else {
            // 5b. second route if cart item doesn't exist; create new CartItem and retrieve MenuItem entity to get the verified price
            createAndPersistNewCartItem(customerCart, selectedMenuItem, addCartItemRequestDto);
        }
        customerCart.setUpdatedAt(OffsetDateTime.now().toInstant());

        return AddCartItemResponse.builder()
                .restaurantId(addCartItemRequestDto.restaurantId())
                .status(customerCart.getStatus())
                .processedCartItems(customerCart.getItems())
                .build();
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
       
    }

    @Transactional
    public Cart getOrCreateActiveCart(Integer customerId, Integer restaurantId) {
        Optional<Cart> cartOptional = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);

        if (cartOptional.isEmpty()) {
            return createAndPersistNewCartForCustomer(customerId, restaurantId);
        }

        Cart activeCart = cartOptional.get();
        Integer existingRestaurantId = activeCart.getRestaurant().getId();

        if (!existingRestaurantId.equals(restaurantId)) {
            throw new CrossRestaurantConflictException(
                    String.format("Active cart exists for restaurant ID %d, but requested item belong to restaurant ID %d",
                            existingRestaurantId, restaurantId)
            );
        }

        return activeCart;
    }

    private Cart createAndPersistNewCartForCustomer(Integer customerId, Integer restaurantId) {
        Customer customer = customerService.getCustomerReferenceById(customerId);
        Restaurant restaurant = restaurantService.getRestaurantReferenceById(restaurantId);

        Cart cart = Cart.builder()
                .createdAt(OffsetDateTime.now().toInstant())
                .status(CartStatus.ACTIVE)
                .restaurant(restaurant)
                .customer(customer)
                .items(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }

    private void createAndPersistNewCartItem(Cart customerCart, MenuItem selectedMenuItem, AddCartItemRequest addCartItemRequestDto) {
        // 1# check restaurant inventory before creating new item
        validateRestaurantInventory(selectedMenuItem, addCartItemRequestDto.quantity());
        CartItem addedCartItem = CartItem.builder()
                .cart(customerCart)
                .menuItem(selectedMenuItem)
                .quantity(addCartItemRequestDto.quantity())
                .price(selectedMenuItem.getPrice()) // Secure price from DB
                .note(addCartItemRequestDto.note())
                .build();

        customerCart.getItems().add(addedCartItem);
    }

    private void increaseExistingCartItemQuantity(CartItem cartItem, MenuItem selectedMenuItem, int requestedQuantity) {
        // 1# check restaurant inventory and update accordingly (validating cumulative quantity)
        int newTotalQuantity = cartItem.getQuantity() + requestedQuantity;
        validateRestaurantInventory(selectedMenuItem, newTotalQuantity);
        // 2# set new Quantity if available in inventory
        cartItem.setQuantity(newTotalQuantity);
    }

    private void validateRestaurantInventory(MenuItem selectedMenuItem, int requestedQuantity) {
        int availableInventory = restaurantService.getMenuItemInventory(selectedMenuItem.getId());
        if (availableInventory < requestedQuantity)
            throw new MenuItemOutOfStock("Menu Item with ID: " + selectedMenuItem.getId() + " is out of Stock.");
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
