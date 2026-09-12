package com.mentorship.hanakoleh.domain.restaurant.validation;

import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemNotOrderableException;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class MenuItemOrderabilityValidator {

    public void validateOrderable(MenuItem menuItem, int quantity) {
        MenuItemOnDemandStatus status = menuItem.getOnDemandStatus();
        if (status == MenuItemOnDemandStatus.UNAVAILABLE || status == MenuItemOnDemandStatus.OUT_OF_STOCK) {
            throw new MenuItemNotOrderableException(
                    ErrorCode.MENU_ITEM_NOT_ORDERABLE.format(menuItem.getId(), status));
        }

        Integer availableQuantity = menuItem.getAvailableQuantity();
        if (availableQuantity != null && quantity > availableQuantity) {
            throw new MenuItemNotOrderableException(
                    ErrorCode.MENU_ITEM_INSUFFICIENT_STOCK.format(availableQuantity, menuItem.getId()));
        }
    }
}