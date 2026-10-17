package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.config.SecurityConfig;
import com.mentorship.hanakoleh.domain.restaurant.dto.CreateItemCategoryRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.CreateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.ItemCategoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateItemCategoryRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.domain.restaurant.service.ItemCategoryService;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuItemService;
import com.mentorship.hanakoleh.handler.GlobalExceptionHandler;
import com.mentorship.hanakoleh.security.CustomUserDetailsService;
import com.mentorship.hanakoleh.security.JwtAuthenticationEntryPoint;
import com.mentorship.hanakoleh.security.JwtAuthenticationFilter;
import com.mentorship.hanakoleh.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemCategoryController.class, MenuItemController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, GlobalExceptionHandler.class})
class RestaurantCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemCategoryService itemCategoryService;

    @MockitoBean
    private MenuItemService menuItemService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/menu-categories returns categories")
    void getCategories_returnsCategoryList() throws Exception {
        ItemCategoryResponse response = new ItemCategoryResponse(1, "Pizza");
        when(itemCategoryService.getCategories()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/menu-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    @DisplayName("POST /api/v1/menu-categories creates category")
    void createCategory_returnsCreatedCategory() throws Exception {
        CreateItemCategoryRequest request = new CreateItemCategoryRequest("Burgers");
        ItemCategoryResponse response = new ItemCategoryResponse(2, "Burgers");
        when(itemCategoryService.createCategory(any(CreateItemCategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/menu-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Burgers"));
    }

    @Test
    @DisplayName("PUT /api/v1/menu-categories/{id} updates category")
    void updateCategory_returnsUpdatedCategory() throws Exception {
        UpdateItemCategoryRequest request = new UpdateItemCategoryRequest("Updated Pizza");
        ItemCategoryResponse response = new ItemCategoryResponse(1, "Updated Pizza");
        when(itemCategoryService.updateCategory(1, any(UpdateItemCategoryRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/menu-categories/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Pizza"));
    }

    @Test
    @DisplayName("DELETE /api/v1/menu-categories/{id} removes category")
    void deleteCategory_returnsNoContent() throws Exception {
        doNothing().when(itemCategoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/v1/menu-categories/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/menu-items returns menu items")
    void getMenuItems_returnsMenuItemList() throws Exception {
        MenuItemResponse response = new MenuItemResponse(1, 10, 1, "Cheese Pizza", "pizza.png",
                new BigDecimal("12.50"), 20, 1, MenuItemOnDemandStatus.AVAILABLE);
        when(menuItemService.getMenuItems()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/menu-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Cheese Pizza"))
                .andExpect(jsonPath("$[0].menuId").value(10));
    }

    @Test
    @DisplayName("POST /api/v1/menu-items creates menu item")
    void createMenuItem_returnsCreatedMenuItem() throws Exception {
        CreateMenuItemRequest request = new CreateMenuItemRequest(10, 1, "Margherita Pizza", "pizza.png",
                new BigDecimal("15.00"), 25, 2, MenuItemOnDemandStatus.AVAILABLE);
        MenuItemResponse response = new MenuItemResponse(2, 10, 1, "Margherita Pizza", "pizza.png",
                new BigDecimal("15.00"), 25, 2, MenuItemOnDemandStatus.AVAILABLE);
        when(menuItemService.createMenuItem(any(CreateMenuItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Margherita Pizza"));
    }

    @Test
    @DisplayName("PUT /api/v1/menu-items/{id} updates menu item")
    void updateMenuItem_returnsUpdatedMenuItem() throws Exception {
        UpdateMenuItemRequest request = new UpdateMenuItemRequest("Updated Pizza", "updated.png",
                new BigDecimal("16.50"), 15, 3, MenuItemOnDemandStatus.PREORDER, 1);
        MenuItemResponse response = new MenuItemResponse(1, 10, 1, "Updated Pizza", "updated.png",
                new BigDecimal("16.50"), 15, 3, MenuItemOnDemandStatus.PREORDER);
        when(menuItemService.updateMenuItem(1, any(UpdateMenuItemRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/menu-items/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Pizza"));
    }

    @Test
    @DisplayName("DELETE /api/v1/menu-items/{id} removes menu item")
    void deleteMenuItem_returnsNoContent() throws Exception {
        doNothing().when(menuItemService).deleteMenuItem(1);

        mockMvc.perform(delete("/api/v1/menu-items/{id}", 1))
                .andExpect(status().isNoContent());
    }
}

