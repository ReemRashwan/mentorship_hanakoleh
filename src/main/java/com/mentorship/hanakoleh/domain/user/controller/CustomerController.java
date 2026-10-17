package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.domain.user.dto.AddPaymentMethodRequest;
import com.mentorship.hanakoleh.domain.user.dto.ChangePasswordRequest;
import com.mentorship.hanakoleh.domain.user.dto.NotificationPreferencesResponse;
import com.mentorship.hanakoleh.domain.user.dto.PaymentMethodResponse;
import com.mentorship.hanakoleh.domain.user.dto.PreferencesResponse;
import com.mentorship.hanakoleh.domain.user.dto.UpdateNotificationPreferencesRequest;
import com.mentorship.hanakoleh.domain.user.dto.UpdatePreferencesRequest;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Customer account, payment methods, and preferences APIs")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

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
        return ResponseEntity.ok(customerService.listPaymentMethods(userPrincipal.getId()));
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
