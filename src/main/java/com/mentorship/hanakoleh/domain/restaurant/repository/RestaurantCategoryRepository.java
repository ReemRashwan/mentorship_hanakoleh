package com.mentorship.hanakoleh.domain.restaurant.repository;

import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory,Integer> {
}
