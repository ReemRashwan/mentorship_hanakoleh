package com.mentorship.hanakoleh.domain.restaurant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.ItemCategory;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MenuItemSearchMapper Unit Tests")
class MenuItemSearchMapperTest {

    private final MenuItemSearchMapper mapper = new MenuItemSearchMapper();

    private MenuItem item(MenuItemOnDemandStatus status, ItemCategory category) {
        Restaurant restaurant = Restaurant.builder().id(1).name("Koshary El Tahrir").build();
        Menu menu = Menu.builder().id(1).restaurant(restaurant).build();
        return MenuItem.builder()
                .id(7)
                .menu(menu)
                .category(category)
                .name("Koshary Large")
                .price(new BigDecimal("55.00"))
                .onDemandStatus(status)
                .build();
    }

    @Test
    @DisplayName("maps all fields including restaurant and category")
    void toResponse_mapsAllFields() {
        ItemCategory category = ItemCategory.builder().id(1).name("Main Dishes").build();
        MenuItemSearchResponse dto = mapper.toResponse(item(MenuItemOnDemandStatus.AVAILABLE, category));

        assertThat(dto.menuItemId()).isEqualTo(7);
        assertThat(dto.name()).isEqualTo("Koshary Large");
        assertThat(dto.restaurantId()).isEqualTo(1);
        assertThat(dto.restaurantName()).isEqualTo("Koshary El Tahrir");
        assertThat(dto.price()).isEqualByComparingTo("55.00");
        assertThat(dto.category()).isEqualTo("Main Dishes");
        assertThat(dto.available()).isTrue();
    }

    @Test
    @DisplayName("null category maps to null; AVAILABLE and PREORDER are available, others are not")
    void toResponse_availabilityAndNullCategory() {
        assertThat(mapper.toResponse(item(MenuItemOnDemandStatus.PREORDER, null)).category()).isNull();
        assertThat(mapper.toResponse(item(MenuItemOnDemandStatus.PREORDER, null)).available()).isTrue();
        assertThat(mapper.toResponse(item(MenuItemOnDemandStatus.OUT_OF_STOCK, null)).available()).isFalse();
        assertThat(mapper.toResponse(item(MenuItemOnDemandStatus.UNAVAILABLE, null)).available()).isFalse();
    }
}
