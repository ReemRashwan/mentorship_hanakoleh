package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateMenuRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateMenuRequest;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenus() {
        return menuRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MenuResponse createMenu(CreateMenuRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + request.restaurantId()));

        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(request.name())
                .icon(request.icon())
                .uiOrder(request.uiOrder() == null ? 0 : request.uiOrder())
                .visible(request.visible() == null || request.visible())
                .build();

        Menu savedMenu = menuRepository.save(menu);
        return toResponse(savedMenu);
    }

    @Transactional
    public MenuResponse updateMenu(Integer id, UpdateMenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with ID: " + id));

        menu.setName(request.name());
        menu.setIcon(request.icon());
        menu.setUiOrder(request.uiOrder() == null ? menu.getUiOrder() : request.uiOrder());
        menu.setVisible(request.visible() == null ? menu.getVisible() : request.visible());

        return toResponse(menuRepository.save(menu));
    }

    @Transactional
    public void deleteMenu(Integer id) {
        if (!menuRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu not found with ID: " + id);
        }
        menuRepository.deleteById(id);
    }

    private MenuResponse toResponse(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getRestaurant() != null ? menu.getRestaurant().getId() : null,
                menu.getName(),
                menu.getIcon(),
                menu.getUiOrder(),
                menu.getVisible()
        );
    }
}

