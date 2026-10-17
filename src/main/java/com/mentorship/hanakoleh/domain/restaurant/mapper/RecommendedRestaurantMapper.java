package com.mentorship.hanakoleh.domain.restaurant.mapper;

import com.mentorship.hanakoleh.domain.restaurant.dto.RecommendedRestaurantResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RecommendedRestaurantMapper {

    public RecommendedRestaurantResponse toResponse(Restaurant restaurant, String reason) {
        return new RecommendedRestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory() == null ? null : restaurant.getCategory().getName(),
                restaurant.getRating(),
                reason);
    }
}
