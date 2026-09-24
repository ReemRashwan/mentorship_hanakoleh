package com.mentorship.hanakoleh.domain.rider.repository;

import com.mentorship.hanakoleh.domain.user.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {
    Optional<Rider> findRiderByUserId(Integer userId);
}
