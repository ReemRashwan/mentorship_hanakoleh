package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.Customer;
import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.user.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }


    public Customer getCustomerReferenceById(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }
        return customerRepository.getReferenceById(customerId);
    }

    public Integer retrieveCustomerIdByUserId(Integer userId) {
        Optional<Integer> returnedCustomerId = customerRepository.findCustomerIdByUserId(userId);
        if (returnedCustomerId.isPresent()) {
            return returnedCustomerId.get();
        } else
            throw new CustomerNotFoundException("Customer not found with User ID: " + userId);

    }

}