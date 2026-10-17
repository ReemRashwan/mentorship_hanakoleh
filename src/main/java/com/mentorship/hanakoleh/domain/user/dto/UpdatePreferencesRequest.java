package com.mentorship.hanakoleh.domain.user.dto;

import com.mentorship.hanakoleh.domain.user.model.PreferredCurrency;
import com.mentorship.hanakoleh.domain.user.model.PreferredLanguage;
import lombok.Builder;

@Builder
public record UpdatePreferencesRequest(
        PreferredLanguage preferredLanguage,
        PreferredCurrency preferredCurrency
) {
}
