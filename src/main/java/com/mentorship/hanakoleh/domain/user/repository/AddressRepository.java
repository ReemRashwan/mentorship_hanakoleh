package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findById(Long addressId);


    boolean existsByCustomerId(Integer customerId);

    @Query("select a from Address a where a.id = :addressId and a.customer.id = :customerId")
    Optional<Address> findByIdAndCustomerId(@Param("addressId") Long addressId,
                                            @Param("customerId") Integer customerId);

    @Query("select a from Address a where a.customer.id = :customerId and a.isDefault = true")
    Optional<Address> findDefaultByCustomerId(@Param("customerId") Integer customerId);

    List<Address> findAllByCustomerId(Integer customerId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.customer.id = :customerId AND a.isDefault = true")
    void clearDefaultAddressByCustomerId(@Param("customerId") Integer customerId);

    Optional<Address> findFirstByCustomerIdAndIdNotOrderByIdDesc(Integer customerId, Long addressId);
}