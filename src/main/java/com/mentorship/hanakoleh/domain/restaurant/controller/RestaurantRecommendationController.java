package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.RecommendedRestaurantResponse;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantRecommendationService;
import com.mentorship.hanakoleh.domain.user.AuthenticationFunction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurant", description = "Restaurant & menu browsing API")
public class RestaurantRecommendationController {

    private final RestaurantRecommendationService recommendationService;

    public RestaurantRecommendationController(RestaurantRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Recommended restaurants for the authenticated customer",
            description = "v1: restaurants you ordered from before, then top-rated restaurants to "
                    + "fill up to limit. Falls back entirely to top-rated when you have no history.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<RecommendedRestaurantResponse>> recommendations(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "limit", required = false) Integer limit) {
        Integer customerId = AuthenticationFunction.extractID(authorizationHeader);
        return ResponseEntity.ok(recommendationService.getRecommendations(customerId, limit));
    }
}
