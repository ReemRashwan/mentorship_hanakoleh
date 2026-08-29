package com.mentorship.hanakoleh.service;

import com.mentorship.hanakoleh.domain.user.GuestUser;
import com.mentorship.hanakoleh.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.repository.UserGuestRepository;
import com.mentorship.hanakoleh.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserGuestRepository userGuestRepository;

    public UserService(UserRepository userRepository, UserGuestRepository userGuestRepository) {
        this.userRepository = userRepository;
        this.userGuestRepository = userGuestRepository;

    }

    public Integer getCustomerIdByUserId(Integer userId) {
        Integer returnedCustomerId = userRepository
                .retrieveCustomerIdByUserId(userId)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found with User Id "+userId));
        return returnedCustomerId;
    }

//    public GuestUser createNewGuestUser(GuestUserDTO guestUserDTO) {
//
//        userRepository.save(user);
//        userGuestRepository.
//    }

    public Optional <GuestUser> retreiveGuestUserIdByToken(String guestUserToken) {
        userGuestRepository.findById(guestUserToken);
        return null;
    }
}
