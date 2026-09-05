package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findCustomerByUserId(Integer userId);

    @Query("SELECT c.id FROM Customer c WHERE c.user.id = :userId")
    Optional<Integer> findCustomerIdByUserId(@Param("userId") Integer userId);
}
