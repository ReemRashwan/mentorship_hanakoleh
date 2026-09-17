package com.mentorship.hanakoleh.domain.restaurant.repository;

import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantDeliveryOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantDeliveryOptionRepository extends JpaRepository<RestaurantDeliveryOption, Integer> {

    @Query("""
            select c from RestaurantDeliveryOption c
            join fetch c.restaurant r
            where r.id = :restaurantId and c.deliveryOption = :option and c.isActive = true
            """)
    Optional<RestaurantDeliveryOption> findActiveWithRestaurant(
            @Param("restaurantId") Integer restaurantId,
            @Param("option") OrderDeliveryOption option);
}