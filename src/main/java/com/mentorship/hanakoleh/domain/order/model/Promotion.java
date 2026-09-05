package com.mentorship.hanakoleh.domain.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    @Column(name = "promotion_code", nullable = false, unique = true, length = 30)
    @NotBlank
    @Size(max = 30)
    private String code;

    @Column(name = "promotion_description", length = 255)
    @Size(max = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_discount_type", nullable = false, length = 20)
    @NotNull
    private PromotionDiscountType discountType;

    @Column(name = "promotion_discount_value", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal discountValue;

    @Column(name = "promotion_max_discount_amount", precision = 12, scale = 4)
    @DecimalMin(value = "0.0000")
    private BigDecimal maxDiscountAmount;

    @Builder.Default
    @Column(name = "promotion_min_order_amount", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "promotion_currency_code", nullable = false, length = 3)
    @NotNull
    @Size(min = 3, max = 3)
    private String currencyCode = "EGP";

    @Column(name = "promotion_usage_limit_total")
    @Min(0)
    private Integer usageLimitTotal;

    @Builder.Default
    @Column(name = "promotion_usage_limit_per_customer", nullable = false)
    @NotNull
    @Min(1)
    private Integer usageLimitPerCustomer = 1;

    @Builder.Default
    @Column(name = "promotion_usage_count_total", nullable = false)
    @NotNull
    @Min(0)
    private Integer usageCountTotal = 0;

    @Column(name = "promotion_starts_at", nullable = false)
    @NotNull
    private OffsetDateTime startsAt;

    @Column(name = "promotion_ends_at", nullable = false)
    @NotNull
    private OffsetDateTime endsAt;

    @Builder.Default
    @Column(name = "promotion_is_active", nullable = false)
    @NotNull
    private Boolean isActive = true;

    @Column(name = "promotion_created_at", nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @Column(name = "promotion_updated_at", nullable = false)
    @NotNull
    private OffsetDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Promotion)) {
            return false;
        }
        Promotion promotion = (Promotion) other;
        return id != null && id.equals(promotion.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
