package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentorship.hanakoleh.config.SecurityConfig;
import com.mentorship.hanakoleh.domain.restaurant.dto.CreateReviewRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.ReviewResponse;
import com.mentorship.hanakoleh.domain.restaurant.service.ReviewService;
import com.mentorship.hanakoleh.handler.GlobalExceptionHandler;
import com.mentorship.hanakoleh.security.CustomUserDetailsService;
import com.mentorship.hanakoleh.security.JwtAuthenticationEntryPoint;
import com.mentorship.hanakoleh.security.JwtAuthenticationFilter;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

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
        ReviewResponse response = new ReviewResponse(1, 10, 8, 5, "Excellent food!", OffsetDateTime.now());

        when(reviewService.addReview(eq(10), eq(8), any(CreateReviewRequest.class))).thenReturn(response);

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
        ReviewResponse response = new ReviewResponse(1, 10, 8, 4, "Very good", OffsetDateTime.now());
        when(reviewService.getReviews(10)).thenReturn(java.util.List.of(response));

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/reviews", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantId").value(10))
                .andExpect(jsonPath("$[0].comment").value("Very good"));
    }
}

