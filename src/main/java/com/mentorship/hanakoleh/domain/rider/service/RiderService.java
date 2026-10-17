package com.mentorship.hanakoleh.domain.rider.service;

import com.mentorship.hanakoleh.domain.rider.exception.RiderNotFoundException;
import com.mentorship.hanakoleh.domain.user.model.Rider;
import com.mentorship.hanakoleh.domain.user.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

    public Rider retrieveRiderByUserId(Integer userId) {
        return riderRepository.findRiderByUserId(userId)
                .orElseThrow(() -> new RiderNotFoundException(
                        String.format("Requested Rider with user id %d not found", userId)));
    }
}

