package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.dto.*;
import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.user.exception.InvalidPasswordException;
import com.mentorship.hanakoleh.domain.user.exception.InvalidTokenException;
import com.mentorship.hanakoleh.domain.user.exception.PaymentMethodNotFoundException;
import com.mentorship.hanakoleh.domain.user.model.*;
import com.mentorship.hanakoleh.domain.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;
    private final PreferencesRepository preferencesRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Customer> getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId);
    }

    public Customer getCustomerReferenceById(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }
        return customerRepository.getReferenceById(customerId);
    }

    public Integer retrieveCustomerIdByUserId(Integer userId) {
        if (!customerRepository.existsById(userId)) {
            throw new CustomerNotFoundException("Customer not found with User ID: " + userId);
        }
        return userId;
    }

    @Transactional
    public ProfileResponse updateProfile(Integer customerId, UpdateProfileRequest request) {
        Customer customer = getCustomerOrThrow(customerId);
        User user = customer.getUser();

        if (request.name() != null) {
            user.setFirstName(request.name());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.profilePictureUrl() != null) {
            customer.setProfilePictureUrl(request.profilePictureUrl());
        }

        userRepository.save(user);
        customerRepository.save(customer);

        return ProfileResponse.builder()
                .id(customer.getId())
                .name(user.getFirstName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(customer.getProfilePictureUrl())
                .accountStatus(customer.getAccountStatus())
                .build();
    }

    @Transactional
    public void changePassword(Integer customerId, ChangePasswordRequest request) {
        Customer customer = getCustomerOrThrow(customerId);
        User user = customer.getUser();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Old password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordReset(Integer customerId) {
        Customer customer = getCustomerOrThrow(customerId);

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(java.time.Duration.ofMinutes(15));

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .customer(customer)
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset token generated for customer ID: {}. Token: {} (email stub - no real email sent)", customerId, token);
    }

    private Customer getCustomerOrThrow(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.getUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token has expired");
        }

        Customer customer = resetToken.getCustomer();
        User user = customer.getUser();

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public PaymentMethodResponse addPaymentMethod(Integer customerId, AddPaymentMethodRequest request) {
        Customer customer = getCustomerOrThrow(customerId);

        if (request.isDefault() != null && request.isDefault()) {
            paymentMethodRepository.findByCustomerId(customerId)
                    .forEach(pm -> {
                        pm.setIsDefault(false);
                        paymentMethodRepository.save(pm);
                    });
        }

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .customer(customer)
                .providerToken(request.providerToken())
                .brand(request.brand())
                .last4(request.last4())
                .expiryMonth(request.expiryMonth())
                .expiryYear(request.expiryYear())
                .isDefault(request.isDefault() != null ? request.isDefault() : false)
                .build();

        paymentMethod = paymentMethodRepository.save(paymentMethod);

        return PaymentMethodResponse.builder()
                .id(paymentMethod.getId())
                .brand(paymentMethod.getBrand())
                .last4(paymentMethod.getLast4())
                .expiryMonth(paymentMethod.getExpiryMonth())
                .expiryYear(paymentMethod.getExpiryYear())
                .isDefault(paymentMethod.getIsDefault())
                .createdAt(paymentMethod.getCreatedAt())
                .build();
    }

    @Transactional
    public void removePaymentMethod(Integer customerId, Integer paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndCustomerId(paymentMethodId, customerId)
                .orElseThrow(() -> new PaymentMethodNotFoundException("Payment method not found or does not belong to customer"));

        paymentMethodRepository.delete(paymentMethod);
    }

    public List<PaymentMethodResponse> listPaymentMethods(Integer customerId) {
        return paymentMethodRepository.findByCustomerId(customerId).stream()
                .map(pm -> PaymentMethodResponse.builder()
                        .id(pm.getId())
                        .brand(pm.getBrand())
                        .last4(pm.getLast4())
                        .expiryMonth(pm.getExpiryMonth())
                        .expiryYear(pm.getExpiryYear())
                        .isDefault(pm.getIsDefault())
                        .createdAt(pm.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public NotificationPreferencesResponse updateNotificationPreferences(Integer customerId, UpdateNotificationPreferencesRequest request) {
        NotificationPreferences preferences = notificationPreferencesRepository.findById(customerId)
                .orElseGet(() -> {
                    Customer customer = getCustomerOrThrow(customerId);
                    return NotificationPreferences.builder()
                            .id(customerId)
                            .customer(customer)
                            .build();
                });

        if (request.emailEnabled() != null) {
            preferences.setEmailEnabled(request.emailEnabled());
        }
        if (request.smsEnabled() != null) {
            preferences.setSmsEnabled(request.smsEnabled());
        }
        if (request.pushEnabled() != null) {
            preferences.setPushEnabled(request.pushEnabled());
        }

        preferences = notificationPreferencesRepository.save(preferences);

        return NotificationPreferencesResponse.builder()
                .emailEnabled(preferences.getEmailEnabled())
                .smsEnabled(preferences.getSmsEnabled())
                .pushEnabled(preferences.getPushEnabled())
                .updatedAt(preferences.getUpdatedAt())
                .build();
    }

    @Transactional
    public PreferencesResponse updatePreferences(Integer customerId, UpdatePreferencesRequest request) {
        Preferences preferences = preferencesRepository.findById(customerId)
                .orElseGet(() -> {
                    Customer customer = getCustomerOrThrow(customerId);
                    return Preferences.builder()
                            .id(customerId)
                            .customer(customer)
                            .build();
                });

        if (request.preferredLanguage() != null) {
            preferences.setPreferredLanguage(request.preferredLanguage());
        }
        if (request.preferredCurrency() != null) {
            preferences.setPreferredCurrency(request.preferredCurrency());
        }

        preferences = preferencesRepository.save(preferences);

        return PreferencesResponse.builder()
                .preferredLanguage(preferences.getPreferredLanguage())
                .preferredCurrency(preferences.getPreferredCurrency())
                .updatedAt(preferences.getUpdatedAt())
                .build();
    }
}