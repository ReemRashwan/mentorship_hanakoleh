package com.mentorship.hanakoleh.repository;

import com.mentorship.hanakoleh.domain.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository <Restaurant, Integer> {
}
