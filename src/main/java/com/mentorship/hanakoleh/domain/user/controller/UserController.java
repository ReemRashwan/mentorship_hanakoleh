package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.domain.user.dto.UpdateUserProfileRequest;
import com.mentorship.hanakoleh.domain.user.dto.UserProfileResponse;
import com.mentorship.hanakoleh.domain.user.model.User;
import com.mentorship.hanakoleh.domain.user.service.UserService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Authenticated user profile retrieval and basic profile updates")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current customer profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(toResponse(userService.getProfile(userPrincipal.getId())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current customer profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                            @Valid @RequestBody UpdateUserProfileRequest request) {
        User user = userService.updateProfile(userPrincipal.getId(), request.firstName(), request.lastName(), request.phoneNumber());
        return ResponseEntity.ok(toResponse(user));
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                userService.getNotificationStatus(user.getId())
        );
    }
}
