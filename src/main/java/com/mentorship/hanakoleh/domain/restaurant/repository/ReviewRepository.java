package com.mentorship.hanakoleh.domain.restaurant.repository;

import com.mentorship.hanakoleh.domain.restaurant.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByRestaurantId(Integer restaurantId);
}

