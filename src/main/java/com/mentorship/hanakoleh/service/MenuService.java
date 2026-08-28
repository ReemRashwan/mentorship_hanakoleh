package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.domain.restaurant.Menu;
import com.mentorship.hanakoleh.domain.restaurant.MenuItem;
import com.mentorship.hanakoleh.repository.MenuItemRepository;
import com.mentorship.hanakoleh.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuRepository menuRepository,MenuItemRepository menuItemRepository){
        this.menuItemRepository=menuItemRepository;
        this.menuRepository=menuRepository;
    }

    MenuItem findMenuItemById(Integer menuItemId){
        //exception need to be updated
        return menuItemRepository.findById( menuItemId).orElseThrow(RuntimeException::new);
    }
}
