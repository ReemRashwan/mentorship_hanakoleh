package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;


    public RestaurantService(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
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
}
