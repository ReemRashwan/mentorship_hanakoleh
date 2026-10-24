package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.model.RestaurantRepresentative;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepresentativeRepository extends JpaRepository<RestaurantRepresentative, Integer> {
    boolean existsByUserIdAndRestaurantId(Integer userId, Integer restaurantId);
}
