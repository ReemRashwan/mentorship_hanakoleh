package com.mentorship.hanakoleh.repository;

import com.mentorship.hanakoleh.domain.restaurant.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository <Menu,Integer> {
}
