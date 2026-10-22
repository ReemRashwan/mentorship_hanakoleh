package com.mentorship.hanakoleh.domain.restaurant.dto;

import lombok.Builder;


@Builder
public record UpdateRestaurantResponse (Integer id, String name) {
}
