package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateMenuItemRequest;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "Menu item management APIs")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping("/menu-items")
    @Operation(summary = "Create a new menu item")
    public ResponseEntity<MenuItemResponse> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.createMenuItem(request));
    }

    @GetMapping("/menu-items")
    @Operation(summary = "Get all menu items")
    public ResponseEntity<List<MenuItemResponse>> getMenuItems() {
        return ResponseEntity.ok(menuItemService.getMenuItems());
    }

    @PutMapping("/menu-items/{id}")
    @Operation(summary = "Update a menu item")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable Integer id,
                                                         @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, request));
    }

    @DeleteMapping("/menu-items/{id}")
    @Operation(summary = "Delete a menu item")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}

