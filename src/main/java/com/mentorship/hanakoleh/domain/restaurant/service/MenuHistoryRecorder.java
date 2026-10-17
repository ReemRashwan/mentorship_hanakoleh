package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuChangeType;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuHistory;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuHistoryRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MenuHistoryRecorder {

    private final MenuHistoryRepository menuHistoryRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void record(Menu menu, MenuChangeType changeType, Integer changedByUserId, String notes) {
        record(menu.getRestaurant().getId(), menu.getId(), menu.getName(), changeType, changedByUserId, notes);
    }

    @Transactional
    public void record(
            Integer restaurantId,
            Integer menuId,
            String menuName,
            MenuChangeType changeType,
            Integer changedByUserId,
            String notes) {

        Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
        MenuHistory entry = MenuHistory.builder()
                .restaurant(restaurant)
                .menuId(menuId)
                .menuName(menuName)
                .changeType(changeType)
                .changedByUserId(changedByUserId)
                .notes(notes)
                .build();
        menuHistoryRepository.save(entry);
    }
}
