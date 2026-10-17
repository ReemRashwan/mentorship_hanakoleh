package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuHistoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.MenuHistoryMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuHistory;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuHistoryRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuHistoryQueryService {

    private final MenuHistoryRepository menuHistoryRepository;
    private final MenuHistoryMapper menuHistoryMapper;

    @Transactional(readOnly = true)
    public Page<MenuHistoryResponse> getHistory(
            Integer restaurantId,
            Integer menuId,
            OffsetDateTime from,
            OffsetDateTime to,
            Pageable pageable) {

        Specification<MenuHistory> spec = (root, query, cb) ->
                cb.equal(root.get("restaurant").get("id"), restaurantId);

        if (menuId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("menuId"), menuId));
        }
        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("changedAt"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("changedAt"), to));
        }

        return menuHistoryRepository.findAll(spec, pageable).map(menuHistoryMapper::toResponse);
    }
}
