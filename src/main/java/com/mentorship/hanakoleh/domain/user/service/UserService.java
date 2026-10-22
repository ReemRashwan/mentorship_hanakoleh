package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.exception.UserAccessDeniedException;
import com.mentorship.hanakoleh.domain.user.exception.UserNotFoundException;
import com.mentorship.hanakoleh.domain.user.model.Customer;
import com.mentorship.hanakoleh.domain.user.model.User;
import com.mentorship.hanakoleh.domain.user.repository.CustomerRepository;
import com.mentorship.hanakoleh.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public User getProfile(Integer userId) {
        return getUserOrThrow(userId);
    }

    @Transactional
    public User updateProfile(Integer userId, String firstName, String lastName, String phoneNumber) {
        User user = getUserOrThrow(userId);
        applyProfileUpdates(user, firstName, lastName, phoneNumber);
        return userRepository.save(user);
    }

    public Boolean getNotificationStatus(Integer userId) {
        return customerRepository.findById(userId)
                .map(Customer::getNotificationStatus)
                .orElse(null);
    }

    private Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    private void applyProfileUpdates(User user, String firstName, String lastName, String phoneNumber) {
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }
    }

    public User getUserOrThrow(Integer userId) {
        return getUserById(userId).orElseThrow(()->new UserNotFoundException("User with ID "+userId+" not found."));
    }

    @Transactional
    public void verifyUserType(Integer userId, String requiredType) {
        User user = getUserOrThrow(userId);

        if (!Objects.equals(user.getUserType().getName(), requiredType)) {
            log.warn("User {} has type {} but {} is required", userId, user.getUserType().getName(), requiredType);
            throw new UserAccessDeniedException("You are not allowed to perform this action");
        }
    }
}
