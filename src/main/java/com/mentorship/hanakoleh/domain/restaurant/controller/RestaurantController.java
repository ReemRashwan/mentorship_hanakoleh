package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuSummaryResponse;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuQueryService;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurant", description = "Restaurant & menu browsing API")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuQueryService menuService;

    public RestaurantController(RestaurantService restaurantService, MenuQueryService menuService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @GetMapping("/{restaurantId}/menus")
    @Operation(summary = "List a restaurant's menus",
            description = "Returns the restaurant's visible menus, ordered for display, each with its item count.")
    public ResponseEntity<List<MenuSummaryResponse>> listMenus(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(menuService.listMenus(restaurantId));
    }
}
