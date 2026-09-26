package com.mentorship.hanakoleh.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateProfileRequest(
        @Size(max = 100)
        String name,
        @Email
        @Size(max = 255)
        String email,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
        @Size(max = 50)
        String phoneNumber,
        @Size(max = 500)
        String profilePictureUrl
) {
}
