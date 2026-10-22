package com.mentorship.hanakoleh.domain.restaurant.mapper;

import com.mentorship.hanakoleh.domain.restaurant.dto.RestaurantDTO;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    public RestaurantDTO toRestaurantDTO(Restaurant restaurant) {
        return new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getPhone(), restaurant.getEmail(), restaurant.getRating(), restaurant.getAvgPreparationTimeInMins(), restaurant.getDeliveryRadiusKm(), restaurant.getReviewCount(), restaurant.getCategory());
    }
}
