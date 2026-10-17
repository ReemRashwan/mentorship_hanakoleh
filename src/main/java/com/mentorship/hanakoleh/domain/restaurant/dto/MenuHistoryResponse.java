package com.mentorship.hanakoleh.domain.restaurant.dto;

import com.mentorship.hanakoleh.domain.restaurant.model.MenuChangeType;
import java.time.OffsetDateTime;

public record MenuHistoryResponse(
        Integer menuId,
        String menuName,
        MenuChangeType changeType,
        Integer changedByUserId,
        OffsetDateTime changedAt,
        String notes) {
}
