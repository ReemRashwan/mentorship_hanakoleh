package com.mentorship.hanakoleh.domain.user.dto;

import com.mentorship.hanakoleh.domain.user.model.AccountStatus;
import lombok.Builder;

@Builder
public record ProfileResponse(
        Integer id,
        String name,
        String email,
        String phoneNumber,
        String profilePictureUrl,
        AccountStatus accountStatus
) {
}
