package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentorship.hanakoleh.config.SecurityConfig;
import com.mentorship.hanakoleh.domain.restaurant.dto.CreateMenuRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateMenuRequest;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuService;
import com.mentorship.hanakoleh.handler.GlobalExceptionHandler;
import com.mentorship.hanakoleh.security.CustomUserDetailsService;
import com.mentorship.hanakoleh.security.JwtAuthenticationEntryPoint;
import com.mentorship.hanakoleh.security.JwtAuthenticationFilter;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, GlobalExceptionHandler.class})
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MenuService menuService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/menus returns menu list")
    void getMenus_returnsMenuList() throws Exception {
        MenuResponse response = new MenuResponse(1, 10, "Lunch Menu", "lunch.png", 0, true);
        when(menuService.getMenus()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].restaurantId").value(10))
                .andExpect(jsonPath("$[0].name").value("Lunch Menu"));
    }

    @Test
    @DisplayName("POST /api/v1/menus creates a menu")
    void createMenu_returnsCreatedMenu() throws Exception {
        CreateMenuRequest request = new CreateMenuRequest(10, "Dinner Menu", "dinner.png", 2, true);
        MenuResponse response = new MenuResponse(2, 10, "Dinner Menu", "dinner.png", 2, true);
        when(menuService.createMenu(any(CreateMenuRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Dinner Menu"))
                .andExpect(jsonPath("$.restaurantId").value(10));
    }

    @Test
    @DisplayName("POST /api/v1/menus rejects invalid payload")
    void createMenu_invalidPayload_returns400() throws Exception {
        String invalidJson = "{\"restaurantId\":null,\"name\":\"\",\"uiOrder\":-1,\"visible\":true}";

        mockMvc.perform(post("/api/v1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/menus/{id} updates menu")
    void updateMenu_returnsUpdatedMenu() throws Exception {
        UpdateMenuRequest request = new UpdateMenuRequest("Updated Menu", "updated.png", 4, false);
        MenuResponse response = new MenuResponse(1, 10, "Updated Menu", "updated.png", 4, false);
        when(menuService.updateMenu(1, any(UpdateMenuRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/menus/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Menu"))
                .andExpect(jsonPath("$.visible").value(false));
    }

    @Test
    @DisplayName("DELETE /api/v1/menus/{id} removes menu")
    void deleteMenu_returnsNoContent() throws Exception {
        doNothing().when(menuService).deleteMenu(1);

        mockMvc.perform(delete("/api/v1/menus/{id}", 1))
                .andExpect(status().isNoContent());
    }
}
