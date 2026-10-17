package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.restaurant.dto.RecommendedRestaurantResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.RecommendedRestaurantMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantRecommendationService {

    static final int DEFAULT_LIMIT = 10;
    static final int MAX_LIMIT = 50;
    static final String REASON_REORDER = "You ordered here before";
    static final String REASON_POPULAR = "Popular right now";

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RecommendedRestaurantMapper recommendedRestaurantMapper;

    @Transactional(readOnly = true)
    public List<RecommendedRestaurantResponse> getRecommendations(Integer customerId, Integer limit) {
        int effectiveLimit = clampLimit(limit);
        List<RecommendedRestaurantResponse> recommendations = new ArrayList<>();

        // Tier 1: restaurants the customer ordered from, most-recent first.
        List<Integer> orderedIds = orderRepository.findDistinctRestaurantIdsOrderedByRecency(
                customerId, PageRequest.of(0, effectiveLimit));
        if (!orderedIds.isEmpty()) {
            Map<Integer, Restaurant> byId = restaurantRepository.findAllById(orderedIds).stream()
                    .collect(Collectors.toMap(Restaurant::getId, Function.identity()));
            for (Integer id : orderedIds) {
                Restaurant restaurant = byId.get(id);
                if (restaurant != null) {
                    recommendations.add(recommendedRestaurantMapper.toResponse(restaurant, REASON_REORDER));
                }
            }
        }

        // Tier 2: fill with top-rated restaurants not already recommended.
        int remaining = effectiveLimit - recommendations.size();
        if (remaining > 0) {
            Specification<Restaurant> spec = (root, query, cb) -> cb.isNotNull(root.get("rating"));
            if (!orderedIds.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.not(root.get("id").in(orderedIds)));
            }
            Sort sort = Sort.by(Sort.Order.desc("rating"), Sort.Order.desc("reviewCount"));
            restaurantRepository.findAll(spec, PageRequest.of(0, remaining, sort))
                    .forEach(r -> recommendations.add(
                            recommendedRestaurantMapper.toResponse(r, REASON_POPULAR)));
        }

        return recommendations;
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
