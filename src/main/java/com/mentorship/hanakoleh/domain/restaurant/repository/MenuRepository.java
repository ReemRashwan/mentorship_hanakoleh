package com.mentorship.hanakoleh.domain.restaurant.repository;

import com.mentorship.hanakoleh.domain.restaurant.model.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    List<Menu> findByRestaurant_IdAndVisibleTrueOrderByUiOrderAscNameAsc(Integer restaurantId);
}
