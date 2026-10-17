package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateReviewRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.ReviewResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Review;
import com.mentorship.hanakoleh.domain.restaurant.service.ReviewService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Restaurant reviews and comments APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{restaurantId}/reviews")
    @Operation(summary = "Add a review and comment for a restaurant")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ReviewResponse> addReview(@PathVariable Integer restaurantId,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @Valid @RequestBody CreateReviewRequest request) {
        Review review = reviewService.addReview(restaurantId, userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(review));
    }

    @GetMapping("/{restaurantId}/reviews")
    @Operation(summary = "View all reviews for a restaurant")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(reviewService.getReviews(restaurantId).stream()
                .map(this::toResponse)
                .toList());
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getRestaurant().getId(),
                review.getCustomer().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
