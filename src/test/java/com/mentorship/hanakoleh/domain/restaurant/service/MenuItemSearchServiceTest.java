package com.mentorship.hanakoleh.domain.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchCriteria;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.MenuItemSearchMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuItemSearchService Unit Tests")
class MenuItemSearchServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private MenuItemSearchMapper menuItemSearchMapper;

    @InjectMocks
    private MenuItemSearchService service;

    private MenuItemSearchCriteria noFilters() {
        return new MenuItemSearchCriteria(null, null, null, null, null, false);
    }

    @Test
    @DisplayName("rejects an inverted price range (min > max) with 400-style IllegalArgumentException")
    void search_invertedPriceRange_throws() {
        MenuItemSearchCriteria c = new MenuItemSearchCriteria(
                null, null, null, new BigDecimal("50"), new BigDecimal("10"), false);

        assertThatThrownBy(() -> service.search(c, "name", null, 0, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minPrice");

        verify(menuItemRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("equal min and max price is allowed")
    void search_equalPriceBounds_ok() {
        MenuItemSearchCriteria c = new MenuItemSearchCriteria(
                null, null, null, new BigDecimal("10"), new BigDecimal("10"), false);
        when(menuItemRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.search(c, "name", null, 0, 20);

        verify(menuItemRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("rejects an unknown sort token")
    void search_invalidSort_throws() {
        assertThatThrownBy(() -> service.search(noFilters(), "calories", null, 0, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid sort");

        verify(menuItemRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("maps the rating sort token to menu.restaurant.rating, descending by default")
    void search_ratingSort_mapsToNestedDesc() {
        when(menuItemRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.search(noFilters(), "rating", null, 0, 20);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(menuItemRepository).findAll(any(Specification.class), captor.capture());
        Sort.Order order = captor.getValue().getSort().getOrderFor("menu.restaurant.rating");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("price sort defaults to ascending and explicit direction is honored")
    void search_priceSort_directionHonored() {
        when(menuItemRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.search(noFilters(), "price", "desc", 0, 20);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(menuItemRepository).findAll(any(Specification.class), captor.capture());
        Sort.Order order = captor.getValue().getSort().getOrderFor("price");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("maps repository results through the mapper")
    void search_mapsResults() {
        MenuItem item = MenuItem.builder().id(7).build();
        Page<MenuItem> page = new PageImpl<>(List.of(item));
        when(menuItemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(menuItemSearchMapper.toResponse(item)).thenReturn(
                new MenuItemSearchResponse(7, "Koshary", 1, "Koshary El Tahrir",
                        new BigDecimal("55.00"), "Main Dishes", true));

        Page<MenuItemSearchResponse> result = service.search(noFilters(), "name", null, 0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).menuItemId()).isEqualTo(7);
    }
}
