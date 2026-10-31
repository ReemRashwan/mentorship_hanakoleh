package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.*;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantSearchCriteria;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuQueryService;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import com.mentorship.hanakoleh.domain.restaurant.service.TopRatedRestaurantService;
import com.mentorship.hanakoleh.domain.user.exception.UserTokenNotFoundException;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurant", description = "Restaurant & menu browsing API")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuQueryService menuQueryService;
    private final TopRatedRestaurantService topRatedRestaurantService;

    public RestaurantController(
            RestaurantService restaurantService,
            MenuQueryService menuQueryService,
            TopRatedRestaurantService topRatedRestaurantService) {
        this.restaurantService = restaurantService;
        this.menuQueryService = menuQueryService;
        this.topRatedRestaurantService = topRatedRestaurantService;
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Top-rated restaurants",
            description = "Highest-rated restaurants for the home/discovery section, sorted by rating "
                    + "desc then review count desc. Optional category and minReviews filters.")
    public ResponseEntity<List<RestaurantSummaryResponse>> topRated(
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minReviews", required = false) Integer minReviews) {
        return ResponseEntity.ok(topRatedRestaurantService.getTopRated(category, minReviews, limit));
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

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AddRestaurantResponse> addRestaurant(@AuthenticationPrincipal UserPrincipal principal,
                                                               @RequestBody @Valid AddRestaurantRequest addRestaurantRequest) {
        if (principal == null) throw new UserTokenNotFoundException("UnAuthorized User");
        Integer userId = principal.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.addRestaurant(userId, addRestaurantRequest));
    }

    @PatchMapping("/{restaurant_id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UpdateRestaurantResponse> updateRestaurant(@AuthenticationPrincipal UserPrincipal principal,
                                                                     @PathVariable(name = "restaurant_id") Integer restaurantId,
                                                                     @RequestBody @Valid UpdateRestaurantRequest updateRestaurantRequest
    ) {
        if (principal == null) throw new UserTokenNotFoundException("UnAuthorized User");
        Integer userId = principal.getId();
        UpdateRestaurantResponse response = restaurantService.updateRestaurant(userId, restaurantId, updateRestaurantRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping({"/{restaurant_id}/enable"})
    public ResponseEntity<Integer> enableRestaurant(@AuthenticationPrincipal UserPrincipal principal,
                                                    @PathVariable(name="restaurant_id") Integer restaurantId) {
        if (principal == null) throw new UserTokenNotFoundException("UnAuthorized User");
        Integer userId = principal.getId();
        restaurantService.enableRestaurant(userId,restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(restaurantId);

    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping({"/{restaurant_id}/disable"})
    public ResponseEntity<Integer> disableRestaurant(@AuthenticationPrincipal UserPrincipal principal,
                                  @PathVariable(name="restaurant_id") Integer restaurantId) {
        if (principal == null) throw new UserTokenNotFoundException("UnAuthorized User");
        Integer userId = principal.getId();
        restaurantService.disableRestaurant(userId,restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(restaurantId);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantDTO>> searchRestaurants(
            @Valid RestaurantSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(criteria, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantDTO>> listRestaurants(@PageableDefault(size = 10, sort = "category") Pageable pageable) {
        return ResponseEntity.ok(restaurantService.listAllRestaurants(pageable));
    }
}
