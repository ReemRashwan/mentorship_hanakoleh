package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRepository extends JpaRepository<Customer, Integer> {

}