package com.mentorship.hanakoleh.domain.restaurant.repository;


import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    @Query("SELECT m.availableQuantity FROM MenuItem m WHERE m.id = :id")
    Optional<Integer> findAvailableQuantityById(@Param("id") Integer id);

}
