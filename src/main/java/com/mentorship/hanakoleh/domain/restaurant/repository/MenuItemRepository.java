package com.mentorship.hanakoleh.domain.restaurant.repository;

import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.projection.MenuItemCountProjection;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    @Query("SELECT m.availableQuantity FROM MenuItem m WHERE m.id = :id")
    Optional<Integer> findAvailableQuantityById(@Param("id") Integer id);

    @Query("SELECT mi.menu.id AS menuId, COUNT(mi) AS itemCount "
            + "FROM MenuItem mi WHERE mi.menu.id IN :menuIds GROUP BY mi.menu.id")
    List<MenuItemCountProjection> countItemsByMenuIds(@Param("menuIds") Collection<Integer> menuIds);
}
