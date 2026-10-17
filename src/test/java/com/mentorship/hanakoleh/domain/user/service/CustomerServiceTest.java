package com.mentorship.hanakoleh.domain.user.service;

import com.mentorship.hanakoleh.domain.user.dto.*;
import com.mentorship.hanakoleh.domain.user.exception.*;
import com.mentorship.hanakoleh.domain.user.model.*;
import com.mentorship.hanakoleh.domain.user.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private NotificationPreferencesRepository notificationPreferencesRepository;

    @Mock
    private PreferencesRepository preferencesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private User user;
    private Language language;
    private UserType userType;

    @BeforeEach
    void setUp() {
        userType = UserType.builder().id(1).name("customer").description("Customer").build();
        language = Language.builder().id(1).name("English").code("EN").build();
        user = User.builder()
                .id(1)
                .userType(userType)
                .email("test@example.com")
                .phoneNumber("+201234567890")
                .firstName("John")
                .lastName("Doe")
                .language(language)
                .passwordHash("hashedPassword")
                .joinedAt(OffsetDateTime.now())
                .build();
        customer = Customer.builder()
                .id(1)
                .user(user)
                .notificationStatus(true)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("Tests for updateProfile")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should successfully update profile")
        void shouldSuccessfullyUpdateProfile() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .name("Jane")
                    .email("jane@example.com")
                    .phoneNumber("+201987654321")
                    .profilePictureUrl("http://example.com/pic.jpg")
                    .build();

            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);

            ProfileResponse response = customerService.updateProfile(1, request);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1);
            assertThat(response.name()).isEqualTo("Jane");
            assertThat(response.email()).isEqualTo("jane@example.com");
            assertThat(response.phoneNumber()).isEqualTo("+201987654321");
            assertThat(response.profilePictureUrl()).isEqualTo("http://example.com/pic.jpg");

            verify(userRepository).save(user);
            verify(customerRepository).save(customer);
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .name("Jane")
                    .build();

            when(customerRepository.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.updateProfile(1, request))
                    .isInstanceOf(CustomerNotFoundException.class)
                    .hasMessage("Customer not found with ID: 1");
        }
    }

    @Nested
    @DisplayName("Tests for changePassword")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should successfully change password with correct old password")
        void shouldSuccessfullyChangePassword() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldPassword")
                    .newPassword("newPassword123")
                    .build();

            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            customerService.changePassword(1, request);

            verify(passwordEncoder).matches("oldPassword", "hashedPassword");
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw InvalidPasswordException when old password does not match")
        void shouldThrowExceptionWhenOldPasswordDoesNotMatch() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("wrongPassword")
                    .newPassword("newPassword123")
                    .build();

            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

            assertThatThrownBy(() -> customerService.changePassword(1, request))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessage("Old password does not match");

            verify(passwordEncoder).matches("wrongPassword", "hashedPassword");
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests for resetPassword")
    class ResetPasswordTests {

        @Test
        @DisplayName("Should successfully reset password with valid token")
        void shouldSuccessfullyResetPassword() {
            PasswordResetToken token = PasswordResetToken.builder()
                    .id(1)
                    .customer(customer)
                    .token("valid-token")
                    .expiresAt(Instant.now().plusSeconds(300))
                    .used(false)
                    .build();

            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .token("valid-token")
                    .newPassword("newPassword123")
                    .build();

            when(passwordResetTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
            when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

            customerService.resetPassword(request);

            assertThat(token.getUsed()).isTrue();
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(user);
            verify(passwordResetTokenRepository).save(token);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when token is expired")
        void shouldThrowExceptionWhenTokenExpired() {
            PasswordResetToken token = PasswordResetToken.builder()
                    .id(1)
                    .customer(customer)
                    .token("expired-token")
                    .expiresAt(Instant.now().minusSeconds(300))
                    .used(false)
                    .build();

            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .token("expired-token")
                    .newPassword("newPassword123")
                    .build();

            when(passwordResetTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

            assertThatThrownBy(() -> customerService.resetPassword(request))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token has expired");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when token is already used")
        void shouldThrowExceptionWhenTokenAlreadyUsed() {
            PasswordResetToken token = PasswordResetToken.builder()
                    .id(1)
                    .customer(customer)
                    .token("used-token")
                    .expiresAt(Instant.now().plusSeconds(300))
                    .used(true)
                    .build();

            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .token("used-token")
                    .newPassword("newPassword123")
                    .build();

            when(passwordResetTokenRepository.findByToken("used-token")).thenReturn(Optional.of(token));

            assertThatThrownBy(() -> customerService.resetPassword(request))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token has already been used");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests for payment methods")
    class PaymentMethodTests {

        @Test
        @DisplayName("Should successfully add payment method")
        void shouldSuccessfullyAddPaymentMethod() {
            AddPaymentMethodRequest request = AddPaymentMethodRequest.builder()
                    .providerToken("tok_visa")
                    .brand("Visa")
                    .last4("4242")
                    .expiryMonth(12)
                    .expiryYear(2025)
                    .isDefault(true)
                    .build();

            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(paymentMethodRepository.findByCustomerId(1)).thenReturn(List.of());
            when(paymentMethodRepository.save(any(PaymentMethod.class))).thenAnswer(invocation -> {
                PaymentMethod pm = invocation.getArgument(0);
                pm.setId(1);
                pm.setCreatedAt(Instant.now());
                pm.setUpdatedAt(Instant.now());
                return pm;
            });

            PaymentMethodResponse response = customerService.addPaymentMethod(1, request);

            assertThat(response).isNotNull();
            assertThat(response.brand()).isEqualTo("Visa");
            assertThat(response.last4()).isEqualTo("4242");
            assertThat(response.isDefault()).isTrue();

            verify(paymentMethodRepository).save(any(PaymentMethod.class));
        }

        @Test
        @DisplayName("Should successfully remove payment method")
        void shouldSuccessfullyRemovePaymentMethod() {
            PaymentMethod paymentMethod = PaymentMethod.builder()
                    .id(1)
                    .customer(customer)
                    .providerToken("tok_visa")
                    .brand("Visa")
                    .last4("4242")
                    .expiryMonth(12)
                    .expiryYear(2025)
                    .isDefault(false)
                    .build();

            when(paymentMethodRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.of(paymentMethod));

            customerService.removePaymentMethod(1, 1);

            verify(paymentMethodRepository).delete(paymentMethod);
        }

        @Test
        @DisplayName("Should throw PaymentMethodNotFoundException when payment method not found or not owned")
        void shouldThrowExceptionWhenPaymentMethodNotFound() {
            when(paymentMethodRepository.findByIdAndCustomerId(1, 1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.removePaymentMethod(1, 1))
                    .isInstanceOf(PaymentMethodNotFoundException.class)
                    .hasMessage("Payment method not found or does not belong to customer");

            verify(paymentMethodRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should successfully list payment methods")
        void shouldSuccessfullyListPaymentMethods() {
            PaymentMethod pm1 = PaymentMethod.builder()
                    .id(1)
                    .customer(customer)
                    .providerToken("tok_visa")
                    .brand("Visa")
                    .last4("4242")
                    .expiryMonth(12)
                    .expiryYear(2025)
                    .isDefault(true)
                    .createdAt(Instant.now())
                    .build();

            PaymentMethod pm2 = PaymentMethod.builder()
                    .id(2)
                    .customer(customer)
                    .providerToken("tok_mastercard")
                    .brand("Mastercard")
                    .last4("5555")
                    .expiryMonth(6)
                    .expiryYear(2024)
                    .isDefault(false)
                    .createdAt(Instant.now())
                    .build();

            when(paymentMethodRepository.findByCustomerId(1)).thenReturn(List.of(pm1, pm2));

            List<PaymentMethodResponse> response = customerService.listPaymentMethods(1);

            assertThat(response).hasSize(2);
            assertThat(response.get(0).brand()).isEqualTo("Visa");
            assertThat(response.get(1).brand()).isEqualTo("Mastercard");
        }
    }

    @Nested
    @DisplayName("Tests for updateNotificationPreferences")
    class UpdateNotificationPreferencesTests {

        @Test
        @DisplayName("Should successfully update notification preferences when they exist")
        void shouldSuccessfullyUpdateNotificationPreferences() {
            NotificationPreferences preferences = NotificationPreferences.builder()
                    .id(1)
                    .customer(customer)
                    .emailEnabled(true)
                    .smsEnabled(true)
                    .pushEnabled(true)
                    .updatedAt(Instant.now())
                    .build();

            UpdateNotificationPreferencesRequest request = UpdateNotificationPreferencesRequest.builder()
                    .emailEnabled(false)
                    .smsEnabled(false)
                    .build();

            when(notificationPreferencesRepository.findById(1)).thenReturn(Optional.of(preferences));
            when(notificationPreferencesRepository.save(any(NotificationPreferences.class))).thenReturn(preferences);

            NotificationPreferencesResponse response = customerService.updateNotificationPreferences(1, request);

            assertThat(response).isNotNull();
            assertThat(response.emailEnabled()).isFalse();
            assertThat(response.smsEnabled()).isFalse();
            assertThat(response.pushEnabled()).isTrue();

            verify(notificationPreferencesRepository).save(preferences);
        }

        @Test
        @DisplayName("Should create notification preferences when they don't exist")
        void shouldCreateNotificationPreferencesWhenNotExists() {
            UpdateNotificationPreferencesRequest request = UpdateNotificationPreferencesRequest.builder()
                    .emailEnabled(false)
                    .build();

            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(notificationPreferencesRepository.findById(1)).thenReturn(Optional.empty());
            when(notificationPreferencesRepository.save(any(NotificationPreferences.class))).thenAnswer(invocation -> {
                NotificationPreferences prefs = invocation.getArgument(0);
                prefs.setUpdatedAt(Instant.now());
                return prefs;
            });

            NotificationPreferencesResponse response = customerService.updateNotificationPreferences(1, request);

            assertThat(response).isNotNull();
            assertThat(response.emailEnabled()).isFalse();

            verify(notificationPreferencesRepository).save(any(NotificationPreferences.class));
        }
    }

    @Nested
    @DisplayName("Tests for updatePreferences")
    class UpdatePreferencesTests {

        @Test
        @DisplayName("Should successfully update preferences when they exist")
        void shouldSuccessfullyUpdatePreferences() {
            Preferences preferences = Preferences.builder()
                    .id(1)
                    .customer(customer)
                    .preferredLanguage(PreferredLanguage.EN)
                    .preferredCurrency(PreferredCurrency.EGP)
                    .updatedAt(Instant.now())
                    .build();

            UpdatePreferencesRequest request = UpdatePreferencesRequest.builder()
                    .preferredLanguage(PreferredLanguage.AR)
                    .preferredCurrency(PreferredCurrency.USD)
                    .build();

            when(preferencesRepository.findById(1)).thenReturn(Optional.of(preferences));
            when(preferencesRepository.save(any(Preferences.class))).thenReturn(preferences);

            PreferencesResponse response = customerService.updatePreferences(1, request);

            assertThat(response).isNotNull();
            assertThat(response.preferredLanguage()).isEqualTo(PreferredLanguage.AR);
            assertThat(response.preferredCurrency()).isEqualTo(PreferredCurrency.USD);

            verify(preferencesRepository).save(preferences);
        }

        @Test
        @DisplayName("Should create preferences when they don't exist")
        void shouldCreatePreferencesWhenNotExists() {
            UpdatePreferencesRequest request = UpdatePreferencesRequest.builder()
                    .preferredLanguage(PreferredLanguage.AR)
                    .build();

            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
            when(preferencesRepository.findById(1)).thenReturn(Optional.empty());
            when(preferencesRepository.save(any(Preferences.class))).thenAnswer(invocation -> {
                Preferences prefs = invocation.getArgument(0);
                prefs.setUpdatedAt(Instant.now());
                return prefs;
            });

            PreferencesResponse response = customerService.updatePreferences(1, request);

            assertThat(response).isNotNull();
            assertThat(response.preferredLanguage()).isEqualTo(PreferredLanguage.AR);

            verify(preferencesRepository).save(any(Preferences.class));
        }
    }
}
