package com.mentorship.hanakoleh.domain.restaurant.dto;

public record MenuSummaryResponse(
        Integer menuId,
        String name,
        String icon,
        Integer uiOrder,
        long itemCount) {
}
