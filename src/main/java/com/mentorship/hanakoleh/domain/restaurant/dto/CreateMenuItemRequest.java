package com.mentorship.hanakoleh.domain.restaurant.dto;

import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateMenuItemRequest(
        @NotNull(message = "Menu ID is required") Integer menuId,
        Integer categoryId,
        @NotBlank(message = "Menu item name is required") @Size(max = 150, message = "Menu item name must not exceed 150 characters") String name,
        @Size(max = 255, message = "Menu item image must not exceed 255 characters") String image,
        @NotNull(message = "Price is required") @DecimalMin(value = "0.00", message = "Price cannot be negative") BigDecimal price,
        @Min(value = 0, message = "Available quantity cannot be negative") Integer availableQuantity,
        @Min(value = 0, message = "UI order cannot be negative") Integer uiOrder,
        MenuItemOnDemandStatus onDemandStatus
) {
}

