package com.mentorship.hanakoleh.domain.user.repository;

import com.mentorship.hanakoleh.domain.user.model.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, Integer> {
    Optional<Preferences> findById(Integer customerId);
}
