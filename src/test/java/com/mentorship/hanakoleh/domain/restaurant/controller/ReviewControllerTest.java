package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentorship.hanakoleh.config.SecurityConfig;
import com.mentorship.hanakoleh.domain.restaurant.dto.CreateReviewRequest;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.Review;
import com.mentorship.hanakoleh.domain.restaurant.service.ReviewService;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.model.User;
import com.mentorship.hanakoleh.handler.GlobalExceptionHandler;
import com.mentorship.hanakoleh.security.CustomUserDetailsService;
import com.mentorship.hanakoleh.security.JwtAuthenticationEntryPoint;
import com.mentorship.hanakoleh.security.JwtAuthenticationFilter;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
import com.mentorship.hanakoleh.security.TokenRevocationService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, GlobalExceptionHandler.class})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private TokenRevocationService tokenRevocationService;

    private final UserPrincipal userPrincipal = new UserPrincipal(
            8,
            "customer@example.com",
            "secret",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
    );

    @Test
    @DisplayName("POST /api/v1/restaurants/{restaurantId}/reviews creates review")
    void addReview_returnsCreatedReview() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(5, "Excellent food!");
        Review review = Review.builder()
                .id(1)
                .restaurant(Restaurant.builder().id(10).build())
                .customer(Customer.builder().id(8).user(User.builder().id(8).email("customer@example.com").build()).build())
                .rating(5)
                .comment("Excellent food!")
                .createdAt(OffsetDateTime.now())
                .build();

        when(reviewService.addReview(eq(10), eq(8), any(CreateReviewRequest.class))).thenReturn(review);

        mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/reviews", 10)
                        .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.restaurantId").value(10))
                .andExpect(jsonPath("$.customerId").value(8))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/restaurants/{restaurantId}/reviews returns reviews")
    void getReviews_returnsReviewList() throws Exception {
        Review review = Review.builder()
                .id(1)
                .restaurant(Restaurant.builder().id(10).build())
                .customer(Customer.builder().id(8).user(User.builder().id(8).email("customer@example.com").build()).build())
                .rating(4)
                .comment("Very good")
                .createdAt(OffsetDateTime.now())
                .build();
        when(reviewService.getReviews(10)).thenReturn(List.of(review));

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/reviews", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantId").value(10))
                .andExpect(jsonPath("$[0].comment").value("Very good"));
    }
}
