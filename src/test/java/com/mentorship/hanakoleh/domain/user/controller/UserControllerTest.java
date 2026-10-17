package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.config.SecurityConfig;
import com.mentorship.hanakoleh.domain.user.dto.UpdateUserProfileRequest;
import com.mentorship.hanakoleh.domain.user.dto.UserProfileResponse;
import com.mentorship.hanakoleh.domain.user.model.User;
import com.mentorship.hanakoleh.domain.user.service.UserService;
import com.mentorship.hanakoleh.handler.GlobalExceptionHandler;
import com.mentorship.hanakoleh.security.CustomUserDetailsService;
import com.mentorship.hanakoleh.security.JwtAuthenticationEntryPoint;
import com.mentorship.hanakoleh.security.JwtAuthenticationFilter;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
import com.mentorship.hanakoleh.security.UserPrincipal;
import com.mentorship.hanakoleh.security.TokenRevocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private TokenRevocationService tokenRevocationService;

    private final UserPrincipal userPrincipal = new UserPrincipal(
            7,
            "customer@example.com",
            "secret",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
    );

    @Test
    @DisplayName("GET /api/v1/users/profile returns profile")
    void getProfile_returnsProfile() throws Exception {
        User user = User.builder()
                .id(7)
                .email("customer@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .phoneNumber("+123456789")
                .build();

        when(userService.getProfile(7)).thenReturn(user);
        when(userService.getNotificationStatus(7)).thenReturn(true);

        mockMvc.perform(get("/api/v1/users/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.email").value("customer@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/profile updates profile")
    void updateProfile_returnsUpdatedProfile() throws Exception {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Jane", "Doe", "+123456789");
        User user = User.builder()
                .id(7)
                .email("customer@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .phoneNumber("+123456789")
                .build();

        when(userService.updateProfile(eq(7), eq("Jane"), eq("Doe"), eq("+123456789"))).thenReturn(user);
        when(userService.getNotificationStatus(7)).thenReturn(true);

        mockMvc.perform(put("/api/v1/users/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.phoneNumber").value("+123456789"));
    }
}
