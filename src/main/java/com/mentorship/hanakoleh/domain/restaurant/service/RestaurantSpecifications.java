package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public final class RestaurantSpecifications {

    public static Specification<Restaurant> from(RestaurantSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c.name() != null && !c.name().isBlank())
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + escapeLike(c.name().toLowerCase()) + "%", '\\'));
            if (c.maxAvgPreparationTimeInMins() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("avgPreparationTimeInMins"), c.maxAvgPreparationTimeInMins()));
            if (c.rating() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), c.rating()));
            if (c.minDeliveryRadiusKm() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("deliveryRadiusKm"), c.minDeliveryRadiusKm()));
            if (c.categoryId() != null)
                predicates.add(cb.equal(root.get("category").get("id"), c.categoryId()));

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}