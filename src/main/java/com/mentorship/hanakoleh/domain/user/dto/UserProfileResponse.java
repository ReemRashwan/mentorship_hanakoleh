package com.mentorship.hanakoleh.domain.user.dto;

public record UserProfileResponse(
        Integer id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Boolean notificationStatus
) {
}

