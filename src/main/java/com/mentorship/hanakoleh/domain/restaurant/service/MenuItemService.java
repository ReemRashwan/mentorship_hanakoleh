package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.model.ItemCategory;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.domain.restaurant.repository.ItemCategoryRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        Menu menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with ID: " + request.menuId()));

        ItemCategory category = null;
        if (request.categoryId() != null) {
            category = itemCategoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.categoryId()));
        }

        MenuItem menuItem = MenuItem.builder()
                .menu(menu)
                .category(category)
                .name(request.name().trim())
                .image(request.image())
                .price(request.price())
                .availableQuantity(request.availableQuantity() == null ? 0 : request.availableQuantity())
                .uiOrder(request.uiOrder() == null ? 0 : request.uiOrder())
                .onDemandStatus(request.onDemandStatus() == null ? MenuItemOnDemandStatus.AVAILABLE : request.onDemandStatus())
                .build();

        return toResponse(menuItemRepository.save(menuItem));
    }

    @Transactional
    public MenuItemResponse updateMenuItem(Integer id, UpdateMenuItemRequest request) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));

        if (request.categoryId() != null) {
            ItemCategory category = itemCategoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + request.categoryId()));
            menuItem.setCategory(category);
        }

        menuItem.setName(request.name().trim());
        menuItem.setImage(request.image());
        menuItem.setPrice(request.price() == null ? menuItem.getPrice() : request.price());
        menuItem.setAvailableQuantity(request.availableQuantity() == null ? menuItem.getAvailableQuantity() : request.availableQuantity());
        menuItem.setUiOrder(request.uiOrder() == null ? menuItem.getUiOrder() : request.uiOrder());
        menuItem.setOnDemandStatus(request.onDemandStatus() == null ? menuItem.getOnDemandStatus() : request.onDemandStatus());

        return toResponse(menuItemRepository.save(menuItem));
    }

    @Transactional
    public void deleteMenuItem(Integer id) {
        if (!menuItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu item not found with ID: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    private MenuItemResponse toResponse(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getMenu() != null ? menuItem.getMenu().getId() : null,
                menuItem.getCategory() != null ? menuItem.getCategory().getId() : null,
                menuItem.getName(),
                menuItem.getImage(),
                menuItem.getPrice(),
                menuItem.getAvailableQuantity(),
                menuItem.getUiOrder(),
                menuItem.getOnDemandStatus()
        );
    }
}

