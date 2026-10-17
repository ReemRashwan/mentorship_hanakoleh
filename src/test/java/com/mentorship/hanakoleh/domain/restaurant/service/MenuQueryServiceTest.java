package com.mentorship.hanakoleh.domain.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuListResponse;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.mapper.MenuMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import com.mentorship.hanakoleh.domain.restaurant.projection.MenuItemCountProjection;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuQueryService Unit Tests")
class MenuQueryServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuQueryService menuQueryService;

    private Integer restaurantId;
    private Pageable pageable;
    private Menu menu1;
    private Menu menu2;

    @BeforeEach
    void setUp() {
        restaurantId = 10;
        pageable = PageRequest.of(0, 20, Sort.by("name"));
        menu1 = Menu.builder().id(1).name("Breakfast").build();
        menu2 = Menu.builder().id(2).name("Drinks").build();
    }

    @Test
    @DisplayName("throws RestaurantNotFoundException when the restaurant does not exist")
    void listMenus_restaurantMissing_throws() {
        when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        assertThatThrownBy(() -> menuQueryService.listMenus(restaurantId, null, pageable))
                .isInstanceOf(RestaurantNotFoundException.class)
                .hasMessageContaining(String.valueOf(restaurantId));

        verify(menuRepository, never()).findByRestaurant_IdAndVisibleTrue(anyInt(), any());
        verify(menuRepository, never())
                .findByRestaurant_IdAndVisibleTrueAndCategory_NameIgnoreCase(anyInt(), any(), any());
    }

    @Test
    @DisplayName("without a category filter it uses the plain visible query and enriches item counts")
    void listMenus_noCategory_usesPlainQueryAndCounts() {
        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        Page<Menu> page = new PageImpl<>(List.of(menu1, menu2), pageable, 2);
        when(menuRepository.findByRestaurant_IdAndVisibleTrue(restaurantId, pageable)).thenReturn(page);
        when(menuItemRepository.countItemsByMenuIds(List.of(1, 2)))
                .thenReturn(List.of(count(1, 3L))); // menu2 intentionally absent -> defaults to 0
        when(menuMapper.toListResponse(eq(menu1), eq(3L)))
                .thenReturn(new MenuListResponse(1, "Breakfast", "Food", true, 3L, OffsetDateTime.now()));
        when(menuMapper.toListResponse(eq(menu2), eq(0L)))
                .thenReturn(new MenuListResponse(2, "Drinks", null, true, 0L, OffsetDateTime.now()));

        Page<MenuListResponse> result = menuQueryService.listMenus(restaurantId, "   ", pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(MenuListResponse::itemCount).containsExactly(3L, 0L);
        verify(menuRepository).findByRestaurant_IdAndVisibleTrue(restaurantId, pageable);
        verify(menuRepository, never())
                .findByRestaurant_IdAndVisibleTrueAndCategory_NameIgnoreCase(anyInt(), any(), any());
    }

    @Test
    @DisplayName("with a category filter it uses the category query with a trimmed name")
    void listMenus_withCategory_usesCategoryQuery() {
        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        Page<Menu> page = new PageImpl<>(List.of(menu1), pageable, 1);
        when(menuRepository.findByRestaurant_IdAndVisibleTrueAndCategory_NameIgnoreCase(
                        restaurantId, "Food", pageable))
                .thenReturn(page);
        when(menuItemRepository.countItemsByMenuIds(List.of(1))).thenReturn(List.of(count(1, 5L)));
        when(menuMapper.toListResponse(eq(menu1), eq(5L)))
                .thenReturn(new MenuListResponse(1, "Breakfast", "Food", true, 5L, OffsetDateTime.now()));

        Page<MenuListResponse> result = menuQueryService.listMenus(restaurantId, "  Food  ", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).category()).isEqualTo("Food");
        verify(menuRepository)
                .findByRestaurant_IdAndVisibleTrueAndCategory_NameIgnoreCase(restaurantId, "Food", pageable);
        verify(menuRepository, never()).findByRestaurant_IdAndVisibleTrue(anyInt(), any());
    }

    @Test
    @DisplayName("an empty page skips the item-count query entirely")
    void listMenus_emptyPage_skipsCountQuery() {
        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        when(menuRepository.findByRestaurant_IdAndVisibleTrue(restaurantId, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<MenuListResponse> result = menuQueryService.listMenus(restaurantId, null, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(menuItemRepository, never()).countItemsByMenuIds(any());
        verify(menuMapper, never()).toListResponse(any(), anyLong());
    }

    private static MenuItemCountProjection count(Integer menuId, Long itemCount) {
        return new MenuItemCountProjection() {
            @Override
            public Integer getMenuId() {
                return menuId;
            }

            @Override
            public Long getItemCount() {
                return itemCount;
            }
        };
    }
}
