package com.mentorship.hanakoleh.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ResetPasswordRequest(
        @NotBlank
        String token,
        @NotBlank
        @Size(min = 8)
        String newPassword
) {
}
