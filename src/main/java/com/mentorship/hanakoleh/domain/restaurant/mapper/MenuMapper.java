package com.mentorship.hanakoleh.domain.restaurant.mapper;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuSummaryResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper {

    public MenuSummaryResponse toSummary(Menu menu, long itemCount) {
        return new MenuSummaryResponse(
                menu.getId(), menu.getName(), menu.getIcon(), menu.getUiOrder(), itemCount);
    }
}
