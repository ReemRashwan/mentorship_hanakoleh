package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuSummaryResponse;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.mapper.MenuMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.projection.MenuItemCountProjection;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuQueryService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuMapper menuMapper;

    @Transactional(readOnly = true)
    public List<MenuSummaryResponse> listMenus(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }

        List<Menu> menus = menuRepository.findByRestaurant_IdAndVisibleTrueOrderByUiOrderAscNameAsc(restaurantId);
        if (menus.isEmpty()) {
            return List.of();
        }

        List<Integer> menuIds = menus.stream().map(Menu::getId).toList();
        Map<Integer, Long> itemCountByMenuId = menuItemRepository.countItemsByMenuIds(menuIds).stream()
                .collect(Collectors.toMap(MenuItemCountProjection::getMenuId, MenuItemCountProjection::getItemCount));

        return menus.stream()
                .map(menu -> menuMapper.toSummary(menu, itemCountByMenuId.getOrDefault(menu.getId(), 0L)))
                .toList();
    }
}
