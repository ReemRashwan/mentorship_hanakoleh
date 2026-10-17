package com.mentorship.hanakoleh.domain.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuHistoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.MenuHistoryMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuChangeType;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuHistory;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuHistoryRepository;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("MenuHistoryQueryService Unit Tests")
class MenuHistoryQueryServiceTest {

    @Mock
    private MenuHistoryRepository menuHistoryRepository;

    @Mock
    private MenuHistoryMapper menuHistoryMapper;

    @InjectMocks
    private MenuHistoryQueryService service;

    private Integer restaurantId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        restaurantId = 10;
        pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "changedAt"));
    }

    @Test
    @DisplayName("queries with a built specification and maps results to the response")
    void getHistory_queriesAndMaps() {
        OffsetDateTime changedAt = OffsetDateTime.now();
        MenuHistory entry = MenuHistory.builder()
                .id(1L)
                .menuId(5)
                .menuName("Main Menu")
                .changeType(MenuChangeType.UPDATED)
                .changedByUserId(3)
                .changedAt(changedAt)
                .notes("edited")
                .build();
        Page<MenuHistory> page = new PageImpl<>(List.of(entry), pageable, 1);
        when(menuHistoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(menuHistoryMapper.toResponse(entry)).thenReturn(
                new MenuHistoryResponse(5, "Main Menu", MenuChangeType.UPDATED, 3, changedAt, "edited"));

        Page<MenuHistoryResponse> result =
                service.getHistory(restaurantId, 5, changedAt.minusDays(7), changedAt, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).changeType()).isEqualTo(MenuChangeType.UPDATED);
        verify(menuHistoryRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("with no optional filters it still queries and returns the mapped page")
    void getHistory_noFilters() {
        when(menuHistoryRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<MenuHistoryResponse> result =
                service.getHistory(restaurantId, null, null, null, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(menuHistoryRepository).findAll(any(Specification.class), eq(pageable));
    }
}
