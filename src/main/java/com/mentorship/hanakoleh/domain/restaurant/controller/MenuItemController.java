package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchCriteria;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuItemSearchService;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
@Tag(name = "Menu Item", description = "Customer-facing menu-item search API")
public class MenuItemController {

    private final MenuItemSearchService menuItemSearchService;
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemSearchService menuItemSearchService, MenuItemService menuItemService) {
        this.menuItemSearchService = menuItemSearchService;
        this.menuItemService = menuItemService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search menu items",
            description = "Search menu items across or within restaurants. All filters are optional "
                    + "and combine with AND. Sort by price | name | rating.")
    public ResponseEntity<Page<MenuItemSearchResponse>> search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "restaurantId", required = false) Integer restaurantId,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "availableOnly", required = false, defaultValue = "false")
                    boolean availableOnly,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        MenuItemSearchCriteria criteria = new MenuItemSearchCriteria(
                query, restaurantId, category, minPrice, maxPrice, availableOnly);
        return ResponseEntity.ok(menuItemSearchService.search(criteria, sort, direction, page, size));
    }


    @PostMapping
    @Operation(summary = "Create a menu item")
    public ResponseEntity<MenuItemResponse> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        MenuItemResponse response = menuItemService.createMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all menu items")
    public ResponseEntity<List<MenuItemResponse>> getMenuItems() {
        return ResponseEntity.ok(menuItemService.getMenuItems());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu item")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable Integer id,
                                                           @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu item")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}

