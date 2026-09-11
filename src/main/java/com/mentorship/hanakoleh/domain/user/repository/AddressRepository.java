package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.model.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("select a from Address a where a.id = :addressId and a.customer.id = :customerId")
    Optional<Address> findByIdAndCustomerId(@Param("addressId") Long addressId,
                                            @Param("customerId") Integer customerId);

    @Query("select a from Address a where a.customer.id = :customerId and a.isDefault = true")
    Optional<Address> findDefaultByCustomerId(@Param("customerId") Integer customerId);
}