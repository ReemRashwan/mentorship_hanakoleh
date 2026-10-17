package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.RestaurantSummaryResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.RestaurantSummaryMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TopRatedRestaurantService {

    static final int DEFAULT_LIMIT = 10;
    static final int MAX_LIMIT = 50;

    private final RestaurantRepository restaurantRepository;
    private final RestaurantSummaryMapper restaurantSummaryMapper;

    @Transactional(readOnly = true)
    public List<RestaurantSummaryResponse> getTopRated(String category, Integer minReviews, Integer limit) {
        int effectiveLimit = clampLimit(limit);

        Sort sort = Sort.by(
                Sort.Order.desc("rating"),
                Sort.Order.desc("reviewCount"));

        Specification<Restaurant> spec = (root, query, cb) -> cb.isNotNull(root.get("rating"));
        if (StringUtils.hasText(category)) {
            String name = category.trim().toLowerCase(Locale.ROOT);
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category").get("name")), name));
        }
        if (minReviews != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("reviewCount"), minReviews));
        }

        return restaurantRepository.findAll(spec, PageRequest.of(0, effectiveLimit, sort))
                .map(restaurantSummaryMapper::toSummary)
                .getContent();
    }

    private int clampLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit < 1) {
            return 1;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
