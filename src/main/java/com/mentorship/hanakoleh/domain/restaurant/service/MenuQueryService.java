package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuListResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Read side for restaurant menus (browse). */
@Service
@RequiredArgsConstructor
public class MenuQueryService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuMapper menuMapper;

    @Transactional(readOnly = true)
    public Page<MenuListResponse> listMenus(Integer restaurantId, String category, Pageable pageable) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }

        Page<Menu> menus = StringUtils.hasText(category)
                ? menuRepository.findByRestaurant_IdAndVisibleTrueAndCategory_NameIgnoreCase(
                        restaurantId, category.trim(), pageable)
                : menuRepository.findByRestaurant_IdAndVisibleTrue(restaurantId, pageable);

        List<Integer> menuIds = menus.getContent().stream().map(Menu::getId).toList();
        Map<Integer, Long> itemCountByMenuId = menuIds.isEmpty()
                ? Map.of()
                : menuItemRepository.countItemsByMenuIds(menuIds).stream()
                        .collect(Collectors.toMap(
                                MenuItemCountProjection::getMenuId, MenuItemCountProjection::getItemCount));

        return menus.map(menu -> menuMapper.toListResponse(
                menu, itemCountByMenuId.getOrDefault(menu.getId(), 0L)));
    }
}
