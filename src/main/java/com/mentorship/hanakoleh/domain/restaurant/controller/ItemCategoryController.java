package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateItemCategoryRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.ItemCategoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateItemCategoryRequest;
import com.mentorship.hanakoleh.domain.restaurant.service.ItemCategoryService;
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
@Tag(name = "Menu Categories", description = "Menu category management APIs")
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    @PostMapping("/menu-categories")
    @Operation(summary = "Create a menu category")
    public ResponseEntity<ItemCategoryResponse> createCategory(@Valid @RequestBody CreateItemCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemCategoryService.createCategory(request));
    }

    @GetMapping("/menu-categories")
    @Operation(summary = "Get all menu categories")
    public ResponseEntity<List<ItemCategoryResponse>> getCategories() {
        return ResponseEntity.ok(itemCategoryService.getCategories());
    }

    @PutMapping("/menu-categories/{id}")
    @Operation(summary = "Update a menu category")
    public ResponseEntity<ItemCategoryResponse> updateCategory(@PathVariable Integer id,
                                                             @Valid @RequestBody UpdateItemCategoryRequest request) {
        return ResponseEntity.ok(itemCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/menu-categories/{id}")
    @Operation(summary = "Delete a menu category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        itemCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

