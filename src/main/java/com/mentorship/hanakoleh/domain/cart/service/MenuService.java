package com.mentorship.hanakoleh.domain.cart.service;

import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuRepository;
import org.springframework.stereotype.Service;


@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuRepository menuRepository, MenuItemRepository menuItemRepository) {
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
    }

    MenuItem findMenuItemById(Integer menuItemId) {
        //exception need to be updated
        return menuItemRepository.findById(menuItemId).orElseThrow(RuntimeException::new);
    }
}
