package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchCriteria;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuItemSearchResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.MenuItemSearchMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItemOnDemandStatus;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MenuItemSearchService {

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "price", "price",
            "name", "name",
            "rating", "menu.restaurant.rating");

    private final MenuItemRepository menuItemRepository;
    private final MenuItemSearchMapper menuItemSearchMapper;

    @Transactional(readOnly = true)
    public Page<MenuItemSearchResponse> search(
            MenuItemSearchCriteria criteria, String sort, String direction, int page, int size) {

        validatePriceRange(criteria.minPrice(), criteria.maxPrice());

        Pageable pageable = PageRequest.of(page, size, buildSort(sort, direction));
        Specification<MenuItem> spec = buildSpecification(criteria);

        return menuItemRepository.findAll(spec, pageable).map(menuItemSearchMapper::toResponse);
    }

    private void validatePriceRange(java.math.BigDecimal min, java.math.BigDecimal max) {
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new IllegalArgumentException("minPrice must be less than or equal to maxPrice");
        }
    }

    private Sort buildSort(String sort, String direction) {
        String token = StringUtils.hasText(sort) ? sort.trim().toLowerCase(Locale.ROOT) : "name";
        String property = SORT_FIELDS.get(token);
        if (property == null) {
            throw new IllegalArgumentException(
                    "Invalid sort '" + sort + "'. Allowed: " + SORT_FIELDS.keySet());
        }
        Sort.Direction dir = resolveDirection(direction, token);
        return Sort.by(dir, property);
    }

    private Sort.Direction resolveDirection(String direction, String token) {
        if (StringUtils.hasText(direction)) {
            return "desc".equalsIgnoreCase(direction.trim())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
        }
        // Sensible defaults: highest rating first, otherwise ascending.
        return "rating".equals(token) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private Specification<MenuItem> buildSpecification(MenuItemSearchCriteria criteria) {
        Specification<MenuItem> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(criteria.query())) {
            String like = "%" + criteria.query().trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), like));
        }
        if (criteria.restaurantId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("menu").get("restaurant").get("id"), criteria.restaurantId()));
        }
        if (StringUtils.hasText(criteria.category())) {
            String category = criteria.category().trim().toLowerCase(Locale.ROOT);
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category").get("name")), category));
        }
        if (criteria.minPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), criteria.minPrice()));
        }
        if (criteria.maxPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), criteria.maxPrice()));
        }
        if (criteria.availableOnly()) {
            List<MenuItemOnDemandStatus> orderable =
                    List.of(MenuItemOnDemandStatus.AVAILABLE, MenuItemOnDemandStatus.PREORDER);
            spec = spec.and((root, query, cb) -> root.get("onDemandStatus").in(orderable));
        }
        return spec;
    }
}
