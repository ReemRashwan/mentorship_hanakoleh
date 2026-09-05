package com.mentorship.hanakoleh.domain.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GuestUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "guest_user_token")
    private String guestUserToken;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User guestUserId;

}
