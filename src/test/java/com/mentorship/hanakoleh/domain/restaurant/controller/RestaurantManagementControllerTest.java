package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.TestcontainersConfiguration;
import com.mentorship.hanakoleh.domain.restaurant.dto.AddRestaurantRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.AddRestaurantResponse;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuQueryService;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import com.mentorship.hanakoleh.domain.restaurant.service.TopRatedRestaurantService;
import com.mentorship.hanakoleh.security.CustomUserDetailsService;
import com.mentorship.hanakoleh.security.TokenRevocationService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import com.mentorship.hanakoleh.security.JwtTokenProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers
@DisplayName("Restaurant Management Integration Test")
class RestaurantControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RestaurantService restaurantService;
    @MockitoBean private MenuQueryService menuQueryService;
    @MockitoBean private TopRatedRestaurantService topRatedRestaurantService;

    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean private TokenRevocationService tokenRevocationService;


    private UsernamePasswordAuthenticationToken authenticatedUser() {
        UserPrincipal principal = new UserPrincipal(
                1, "user@example.com", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    private AddRestaurantRequest validRequest() {
        // order must match the record's component order
        return new AddRestaurantRequest(
                "Pizza Hut", "+201234567890", "pizzahut@google.com",
                new BigDecimal("31.2357"), new BigDecimal("30.0444"),
                new BigDecimal("5.0"), 25, 5);
    }

    @Test
    @DisplayName("POST /api/v1/restaurants creates a restaurant when authenticated")
    void testAddRestaurant() throws Exception {
        given(restaurantService.addRestaurant(eq(1), any(AddRestaurantRequest.class)))
                .willReturn(AddRestaurantResponse.builder()
                        .restaurantId(10).restaurantName("Pizza Hut").build());

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(authentication(authenticatedUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.restaurantId").value(10));
    }

    @Test
    @DisplayName("should returns 400 for an invalid body")
    void testAddRestaurant_invalidBody() throws Exception {
        String invalid = """
                {"name":"","phone":"+201234567890","email":"not-an-email",
                 "longitude":31.2,"latitude":120,"restaurantCategoryId":5}
                """;

        mockMvc.perform(post("/api/v1/restaurants")
                        .with(authentication(authenticatedUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/restaurants rejects unauthenticated requests")
    void testAddRestaurant_unauthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/restaurants")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/v1/restaurants/{id}/enable enables the restaurant")
    void testEnableRestaurant() throws Exception {
        mockMvc.perform(patch("/api/v1/restaurants/1/enable")
                        .with(authentication(authenticatedUser()))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(restaurantService).enableRestaurant(eq(1), eq(1));   // (userId, restaurantId)
    }
}