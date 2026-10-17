package com.mentorship.hanakoleh.domain.user.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record NotificationPreferencesResponse(
        Boolean emailEnabled,
        Boolean smsEnabled,
        Boolean pushEnabled,
        Instant updatedAt
) {
}
