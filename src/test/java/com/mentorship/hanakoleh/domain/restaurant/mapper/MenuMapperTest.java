package com.mentorship.hanakoleh.domain.restaurant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuListResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuCategory;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MenuMapper Unit Tests")
class MenuMapperTest {

    private final MenuMapper mapper = new MenuMapper();

    @Test
    @DisplayName("maps all fields; category name is taken from the linked category")
    void toListResponse_withCategory() {
        OffsetDateTime updatedAt = OffsetDateTime.now();
        MenuCategory category = MenuCategory.builder().id(1).name("Food").build();
        Menu menu = Menu.builder()
                .id(7)
                .name("Breakfast")
                .category(category)
                .visible(true)
                .updatedAt(updatedAt)
                .build();

        MenuListResponse dto = mapper.toListResponse(menu, 4L);

        assertThat(dto.menuId()).isEqualTo(7);
        assertThat(dto.name()).isEqualTo("Breakfast");
        assertThat(dto.category()).isEqualTo("Food");
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.itemCount()).isEqualTo(4L);
        assertThat(dto.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("null category -> null category name; visible=false -> isActive=false")
    void toListResponse_noCategory_notVisible() {
        Menu menu = Menu.builder()
                .id(8)
                .name("Hidden")
                .category(null)
                .visible(false)
                .updatedAt(OffsetDateTime.now())
                .build();

        MenuListResponse dto = mapper.toListResponse(menu, 0L);

        assertThat(dto.category()).isNull();
        assertThat(dto.isActive()).isFalse();
        assertThat(dto.itemCount()).isZero();
    }
}
