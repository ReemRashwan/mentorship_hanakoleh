package com.mentorship.hanakoleh.domain.restaurant.mapper;

import com.mentorship.hanakoleh.domain.restaurant.dto.RestaurantSummaryResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantSummaryMapper {

    public RestaurantSummaryResponse toSummary(Restaurant restaurant) {
        return new RestaurantSummaryResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory() == null ? null : restaurant.getCategory().getName(),
                restaurant.getRating(),
                restaurant.getReviewCount());
    }
}
