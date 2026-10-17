package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuListResponse;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuQueryService;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurant", description = "Restaurant & menu browsing API")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuQueryService menuQueryService;

    public RestaurantController(RestaurantService restaurantService, MenuQueryService menuQueryService) {
        this.restaurantService = restaurantService;
        this.menuQueryService = menuQueryService;
    }

    @GetMapping("/{restaurantId}/menus")
    @Operation(summary = "List a restaurant's menus",
            description = "Returns the restaurant's visible menus (paginated, name asc by default), "
                    + "optionally filtered by category, each with its item count.")
    public ResponseEntity<Page<MenuListResponse>> listMenus(
            @PathVariable Integer restaurantId,
            @RequestParam(value = "category", required = false) String category,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(menuQueryService.listMenus(restaurantId, category, pageable));
    }
}
