package com.mentorship.hanakoleh.domain.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMenuRequest(
        @NotBlank(message = "Menu name is required") @Size(max = 150, message = "Menu name must not exceed 150 characters") String name,
        @Size(max = 255, message = "Menu icon must not exceed 255 characters") String icon,
        @Min(value = 0, message = "UI order must be zero or greater") Integer uiOrder,
        Boolean visible
) {
}

