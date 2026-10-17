package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.CreateItemCategoryRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.ItemCategoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateItemCategoryRequest;
import com.mentorship.hanakoleh.domain.restaurant.model.ItemCategory;
import com.mentorship.hanakoleh.domain.restaurant.repository.ItemCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCategoryService {

    private final ItemCategoryRepository itemCategoryRepository;

    @Transactional(readOnly = true)
    public List<ItemCategoryResponse> getCategories() {
        return itemCategoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ItemCategoryResponse createCategory(CreateItemCategoryRequest request) {
        ItemCategory category = ItemCategory.builder()
                .name(request.name().trim())
                .build();

        return toResponse(itemCategoryRepository.save(category));
    }

    @Transactional
    public ItemCategoryResponse updateCategory(Integer id, UpdateItemCategoryRequest request) {
        ItemCategory category = itemCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        category.setName(request.name().trim());
        return toResponse(itemCategoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Integer id) {
        if (!itemCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with ID: " + id);
        }
        itemCategoryRepository.deleteById(id);
    }

    private ItemCategoryResponse toResponse(ItemCategory category) {
        return new ItemCategoryResponse(category.getId(), category.getName());
    }
}

