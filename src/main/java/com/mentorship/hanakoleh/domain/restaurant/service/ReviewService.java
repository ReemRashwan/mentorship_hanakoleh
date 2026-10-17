package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateReviewRequest;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.Review;
import com.mentorship.hanakoleh.domain.restaurant.repository.ReviewRepository;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantService restaurantService;
    private final CustomerRepository customerRepository;

    @Transactional
    public Review addReview(Integer restaurantId, Integer customerId, CreateReviewRequest request) {
        Restaurant restaurant = restaurantService.getRestaurantReferenceById(restaurantId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        Review review = Review.builder()
                .restaurant(restaurant)
                .customer(customer)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        return reviewRepository.save(review);
    }

    @Transactional
    public List<Review> getReviews(Integer restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }
}
