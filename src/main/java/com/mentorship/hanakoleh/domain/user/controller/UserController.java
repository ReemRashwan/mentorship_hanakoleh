package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.domain.user.dto.*;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for customer profile, password, payment methods, and preferences")
public class UserController {

    private final CustomerService customerService;

    @PatchMapping("/profile")
    @Operation(summary = "Update customer profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProfileResponse response = customerService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    @Operation(summary = "Change password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        customerService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset-request")
    @Operation(summary = "Request password reset")
    public ResponseEntity<Void> requestPasswordReset(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        customerService.requestPasswordReset(userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "List payment methods")
    public ResponseEntity<List<PaymentMethodResponse>> listPaymentMethods(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<PaymentMethodResponse> response = customerService.listPaymentMethods(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment-methods")
    @Operation(summary = "Add payment method")
    public ResponseEntity<PaymentMethodResponse> addPaymentMethod(
            @Valid @RequestBody AddPaymentMethodRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PaymentMethodResponse response = customerService.addPaymentMethod(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/payment-methods/{paymentMethodId}")
    @Operation(summary = "Remove payment method")
    public ResponseEntity<Void> removePaymentMethod(
            @PathVariable Integer paymentMethodId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        customerService.removePaymentMethod(userPrincipal.getId(), paymentMethodId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/notifications")
    @Operation(summary = "Update notification preferences")
    public ResponseEntity<NotificationPreferencesResponse> updateNotificationPreferences(
            @RequestBody UpdateNotificationPreferencesRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        NotificationPreferencesResponse response = customerService.updateNotificationPreferences(userPrincipal.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/preferences")
    @Operation(summary = "Update preferences")
    public ResponseEntity<PreferencesResponse> updatePreferences(
            @RequestBody UpdatePreferencesRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PreferencesResponse response = customerService.updatePreferences(userPrincipal.getId(), request);
        return ResponseEntity.ok(response);
    }
}
