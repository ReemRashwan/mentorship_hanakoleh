package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateMenuRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateMenuRequest;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuService;
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
@Tag(name = "Menu Management", description = "Menu management APIs")
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/menus")
    @Operation(summary = "Create a new menu")
    public ResponseEntity<MenuResponse> createMenu(@Valid @RequestBody CreateMenuRequest request) {
        MenuResponse response = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/menus/{id}")
    @Operation(summary = "Update an existing menu")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable Integer id,
                                                 @Valid @RequestBody UpdateMenuRequest request) {
        MenuResponse response = menuService.updateMenu(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/menus/{id}")
    @Operation(summary = "Delete a menu")
    public ResponseEntity<Void> deleteMenu(@PathVariable Integer id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/menus")
    @Operation(summary = "View all menus")
    public ResponseEntity<List<MenuResponse>> getMenus() {
        return ResponseEntity.ok(menuService.getMenus());
    }
}

