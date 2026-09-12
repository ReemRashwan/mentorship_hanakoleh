package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.order.event.OrderConfirmedEvent;
import com.mentorship.hanakoleh.domain.order.service.OrderStatusUpdateService;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
@Slf4j
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderStatusUpdateService orderStatusUpdateService;

    public RestaurantService(OrderStatusUpdateService orderStatusUpdateService, RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.orderStatusUpdateService = orderStatusUpdateService;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Restaurant getRestaurantById(Integer restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    public Restaurant getRestaurantReferenceById(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }
        return restaurantRepository.getReferenceById(restaurantId);
    }

    public Optional<MenuItem> getMenuItemByMenuItemId(@NotEmpty Integer menuItemId) {
        return menuItemRepository.findById(menuItemId);
    }

    public Integer getMenuItemInventory(Integer menuItemId) {
        Optional<Integer> availableQuantity = menuItemRepository.findAvailableQuantityById(menuItemId);
        return availableQuantity.orElseThrow(() -> new EntityNotFoundException("Quantity for Menu Item with ID " + menuItemId + " not found."));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleIncomingConfirmedOrders(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("Incoming Order{}that is {}is received. \n1-matching the order to correct restaurant. 2-checking restaurant status and ability to process.\n3-pushing notification to restaurant.", orderConfirmedEvent.getOrderId(), orderConfirmedEvent.getOrderFinalStatus());
    };

    public void acceptOrderByRestaurant(Integer authenticatedRestaurantId, Long orderId, String notes) {
        orderStatusUpdateService.acceptOrder(authenticatedRestaurantId, orderId, notes);
    }
}
