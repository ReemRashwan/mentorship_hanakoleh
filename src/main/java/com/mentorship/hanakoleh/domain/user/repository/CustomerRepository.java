package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Since customer.id is now the same as user_id (via @MapsId),
    // you can directly use findById(userId) to get the customer by user ID.
    // No additional query methods needed.
}
