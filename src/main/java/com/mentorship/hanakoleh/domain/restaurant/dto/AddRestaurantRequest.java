package com.mentorship.hanakoleh.domain.restaurant.dto;

import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class AddRestaurantRequest {
    //Restaurant restaurant = Restaurant.builder().id().name().phone().email().rating().longitude().latitude().avgPreparationTimeInMins().createdAt().deliveryRadiusKm().build()
    @NotBlank
    String name;
    @NotBlank
    String phone;
    @NotBlank
    String email;
    BigDecimal longitude;
    BigDecimal latitude;
    Integer avgPreparationTimeInMins;



}
