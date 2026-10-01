package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId);
    }


    public Customer getCustomerReferenceById(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }
        return customerRepository.getReferenceById(customerId);
    }

    public Integer retrieveCustomerIdByUserId(Integer userId) {
        // Since customer.id is now the same as user_id via @MapsId,
        // we can directly return the userId after verifying the customer exists
        if (!customerRepository.existsById(userId)) {
            throw new CustomerNotFoundException("Customer not found with User ID: " + userId);
        }
        return userId;
    }

}