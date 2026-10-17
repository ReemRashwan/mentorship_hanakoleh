package com.mentorship.hanakoleh.domain.restaurant.mapper;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import org.springframework.stereotype.Component;

@Component
public class MenuItemSearchMapper {

    public MenuItemSearchResponse toResponse(MenuItem item) {
        return new MenuItemSearchResponse(
                item.getId(),
                item.getName(),
                item.getMenu().getRestaurant().getId(),
                item.getMenu().getRestaurant().getName(),
                item.getPrice(),
                item.getCategory() == null ? null : item.getCategory().getName(),
                isOrderable(item.getOnDemandStatus()));
    }

    public static boolean isOrderable(MenuItemOnDemandStatus status) {
        return status == MenuItemOnDemandStatus.AVAILABLE
                || status == MenuItemOnDemandStatus.PREORDER;
    }
}
