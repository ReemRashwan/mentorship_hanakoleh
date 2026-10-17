package com.mentorship.hanakoleh.domain.cart.service;

import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemUnavailableException;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final RestaurantService restaurantService;

    public MenuItem findMenuItemById(Integer menuItemId) {
        return restaurantService.getMenuItemByMenuItemId(menuItemId)
                .orElseThrow(() -> new MenuItemUnavailableException(
                        "Menu Item with ID: " + menuItemId + " is not available."));
    }
}
