package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    List<PaymentMethod> findByCustomerId(Integer customerId);
    
    Optional<PaymentMethod> findByIdAndCustomerId(Integer paymentMethodId, Integer customerId);
    
    boolean existsByIdAndCustomerId(Integer paymentMethodId, Integer customerId);
}
