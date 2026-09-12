package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public Integer getCustomerIdByUserId(Integer userId) {
        Integer returnedCustomerId = userRepository
                .retrieveCustomerIdByUserId(userId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with User Id " + userId));
        return returnedCustomerId;
    }

//    public GuestUser createNewGuestUser(GuestUserDTO guestUserDTO) {
//
//        userRepository.save(user);
//        userGuestRepository.
//    }
}
