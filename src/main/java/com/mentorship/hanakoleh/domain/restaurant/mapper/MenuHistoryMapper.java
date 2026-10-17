package com.mentorship.hanakoleh.domain.restaurant.mapper;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuHistoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuHistory;
import org.springframework.stereotype.Component;

@Component
public class MenuHistoryMapper {

    public MenuHistoryResponse toResponse(MenuHistory history) {
        return new MenuHistoryResponse(
                history.getMenuId(),
                history.getMenuName(),
                history.getChangeType(),
                history.getChangedByUserId(),
                history.getChangedAt(),
                history.getNotes());
    }
}
