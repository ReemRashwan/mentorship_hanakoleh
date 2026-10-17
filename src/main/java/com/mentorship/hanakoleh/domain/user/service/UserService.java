package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.model.User;
import com.mentorship.hanakoleh.domain.user.repository.CustomerRepository;
import com.mentorship.hanakoleh.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public User getProfile(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    @Transactional
    public User updateProfile(Integer userId, String firstName, String lastName, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }

        return userRepository.save(user);
    }

    public Boolean getNotificationStatus(Integer userId) {
        return customerRepository.findById(userId)
                .map(Customer::getNotificationStatus)
                .orElse(null);
    }
}
