package com.mentorship.hanakoleh.domain.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    private Customer customer;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    @NotBlank
    private String token;

    @Column(name = "expires_at", nullable = false)
    @NotNull
    private Instant expiresAt;

    @Builder.Default
    @Column(name = "used", nullable = false)
    @NotNull
    private Boolean used = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull
    private Instant createdAt;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PasswordResetToken)) {
            return false;
        }
        PasswordResetToken passwordResetToken = (PasswordResetToken) other;
        return id != null && id.equals(passwordResetToken.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
