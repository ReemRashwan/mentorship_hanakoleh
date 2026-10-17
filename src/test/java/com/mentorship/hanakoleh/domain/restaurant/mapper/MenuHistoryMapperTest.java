package com.mentorship.hanakoleh.domain.restaurant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuHistoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuChangeType;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuHistory;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MenuHistoryMapper Unit Tests")
class MenuHistoryMapperTest {

    private final MenuHistoryMapper mapper = new MenuHistoryMapper();

    @Test
    @DisplayName("maps all history fields to the response")
    void toResponse_mapsAllFields() {
        OffsetDateTime changedAt = OffsetDateTime.now();
        MenuHistory history = MenuHistory.builder()
                .id(3L)
                .menuId(5)
                .menuName("Main Menu")
                .changeType(MenuChangeType.DISABLED)
                .changedByUserId(42)
                .changedAt(changedAt)
                .notes("hidden")
                .build();

        MenuHistoryResponse dto = mapper.toResponse(history);

        assertThat(dto.menuId()).isEqualTo(5);
        assertThat(dto.menuName()).isEqualTo("Main Menu");
        assertThat(dto.changeType()).isEqualTo(MenuChangeType.DISABLED);
        assertThat(dto.changedByUserId()).isEqualTo(42);
        assertThat(dto.changedAt()).isEqualTo(changedAt);
        assertThat(dto.notes()).isEqualTo("hidden");
    }

    @Test
    @DisplayName("tolerates null menuId / changedByUserId / notes (deleted menu, system change)")
    void toResponse_nullableFields() {
        MenuHistory history = MenuHistory.builder()
                .id(4L)
                .menuId(null)
                .menuName("Old Menu")
                .changeType(MenuChangeType.DELETED)
                .changedByUserId(null)
                .changedAt(OffsetDateTime.now())
                .notes(null)
                .build();

        MenuHistoryResponse dto = mapper.toResponse(history);

        assertThat(dto.menuId()).isNull();
        assertThat(dto.changedByUserId()).isNull();
        assertThat(dto.notes()).isNull();
        assertThat(dto.changeType()).isEqualTo(MenuChangeType.DELETED);
    }
}
