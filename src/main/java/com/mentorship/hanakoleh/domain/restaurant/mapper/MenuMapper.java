package com.mentorship.hanakoleh.domain.restaurant.mapper;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuListResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper {

    public MenuListResponse toListResponse(Menu menu, long itemCount) {
        return new MenuListResponse(
                menu.getId(),
                menu.getName(),
                menu.getCategory() == null ? null : menu.getCategory().getName(),
                Boolean.TRUE.equals(menu.getVisible()),
                itemCount,
                menu.getUpdatedAt());
    }
}
