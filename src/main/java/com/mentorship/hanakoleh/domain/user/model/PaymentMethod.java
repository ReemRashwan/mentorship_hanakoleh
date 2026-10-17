package com.mentorship.hanakoleh.domain.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    private Customer customer;

    @Column(name = "provider_token", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String providerToken;

    @Column(name = "brand", nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String brand;

    @Column(name = "last4", nullable = false, length = 4)
    @NotBlank
    @Size(max = 4)
    private String last4;

    @Column(name = "expiry_month", nullable = false)
    @NotNull
    private Integer expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    @NotNull
    private Integer expiryYear;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    @NotNull
    private Boolean isDefault = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private Instant updatedAt;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PaymentMethod)) {
            return false;
        }
        PaymentMethod paymentMethod = (PaymentMethod) other;
        return id != null && id.equals(paymentMethod.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
