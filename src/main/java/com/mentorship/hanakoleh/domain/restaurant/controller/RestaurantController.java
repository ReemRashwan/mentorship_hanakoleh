package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.OrderDTO;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="v1/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;

    }
    @PatchMapping("{restaurantId}/orders/{orderId}/accept")
    ResponseEntity<String> acceptOrder(
            @PathVariable Integer restaurantId,
            @PathVariable Long orderId,
            @RequestBody String notes) {
        restaurantService.acceptOrderByRestaurant(restaurantId,orderId,notes);
        return ResponseEntity.ok("Order has been accepted");
    }
}
