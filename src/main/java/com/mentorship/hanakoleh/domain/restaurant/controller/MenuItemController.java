package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchCriteria;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchResponse;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuItemSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menu-items")
@Tag(name = "Menu Item", description = "Customer-facing menu-item search API")
public class MenuItemController {

    private final MenuItemSearchService menuItemSearchService;

    public MenuItemController(MenuItemSearchService menuItemSearchService) {
        this.menuItemSearchService = menuItemSearchService;
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
}
