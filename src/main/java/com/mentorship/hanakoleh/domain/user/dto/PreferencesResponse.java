package com.mentorship.hanakoleh.domain.user.dto;

import com.mentorship.hanakoleh.domain.user.model.PreferredCurrency;
import com.mentorship.hanakoleh.domain.user.model.PreferredLanguage;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PreferencesResponse(
        PreferredLanguage preferredLanguage,
        PreferredCurrency preferredCurrency,
        Instant updatedAt
) {
}
