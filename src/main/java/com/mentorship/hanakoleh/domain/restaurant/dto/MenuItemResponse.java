package com.mentorship.hanakoleh.domain.restaurant.dto;

import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;

import java.math.BigDecimal;

public record MenuItemResponse(
        Integer id,
        Integer menuId,
        Integer categoryId,
        String name,
        String image,
        BigDecimal price,
        Integer availableQuantity,
        Integer uiOrder,
        MenuItemOnDemandStatus onDemandStatus
) {
}

