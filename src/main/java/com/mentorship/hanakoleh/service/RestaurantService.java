package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.domain.restaurant.Restaurant;
import com.mentorship.hanakoleh.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    Restaurant getRestaurantById(Integer restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId));
    }

    Restaurant getRestaurantReferenceById(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }
        return restaurantRepository.getReferenceById(restaurantId);
    }

}
