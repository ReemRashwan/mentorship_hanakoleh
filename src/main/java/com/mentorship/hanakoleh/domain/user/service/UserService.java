package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.dto.UpdateUserProfileRequest;
import com.mentorship.hanakoleh.domain.user.dto.UserProfileResponse;
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
    public UserProfileResponse getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Integer userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        User savedUser = userRepository.save(user);
        return toProfileResponse(savedUser);
    }

    private UserProfileResponse toProfileResponse(User user) {
        Boolean notificationStatus = customerRepository.findById(user.getId())
                .map(Customer::getNotificationStatus)
                .orElse(null);

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                notificationStatus
        );
    }
}
