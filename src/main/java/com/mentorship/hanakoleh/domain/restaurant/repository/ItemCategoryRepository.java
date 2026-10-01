package com.mentorship.hanakoleh.domain.restaurant.repository;

import com.mentorship.hanakoleh.domain.restaurant.model.ItemCategory;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Integer> {
}
