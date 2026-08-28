package com.mentorship.hanakoleh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mentorship.hanakoleh.domain.user.GuestUser;

@Repository
public interface UserGuestRepository extends JpaRepository<GuestUser,String> {
}
