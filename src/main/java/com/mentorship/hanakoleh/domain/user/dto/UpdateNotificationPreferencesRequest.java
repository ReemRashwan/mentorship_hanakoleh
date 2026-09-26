package com.mentorship.hanakoleh.domain.user.dto;

import lombok.Builder;

@Builder
public record UpdateNotificationPreferencesRequest(
        Boolean emailEnabled,
        Boolean smsEnabled,
        Boolean pushEnabled
) {
}
