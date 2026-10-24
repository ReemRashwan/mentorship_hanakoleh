package com.mentorship.hanakoleh.domain.restaurant.dto;

import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantCategory;
import lombok.Builder;

@Builder
public record AddRestaurantResponse (
    Integer restaurantId,
    String restaurantName,
    RestaurantCategory restaurantCategory
){

}
