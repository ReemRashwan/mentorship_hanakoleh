package com.mentorship.hanakoleh.domain.restaurant.repository;

import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    Page<Menu> findByRestaurant_IdAndVisibleTrue(Integer restaurantId, Pageable pageable);

    Page<Menu> findByRestaurant_IdAndVisibleTrueAndCategory_NameIgnoreCase(
            Integer restaurantId, String categoryName, Pageable pageable);
}
