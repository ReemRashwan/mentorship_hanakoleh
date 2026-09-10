package com.mentorship.hanakoleh.domain.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
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
@Table(name = "order_refunds")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_refund_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal amount;

    @Column(name = "refund_reason", nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String reason;

    @Column(name = "refund_gateway_reference", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String gatewayReference;

    @Column(name = "refund_initiated_by_user_id", nullable = false)
    @NotNull
    private Integer initiatedByUserId;

    @Column(name = "refund_created_at", nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderRefund)) {
            return false;
        }
        OrderRefund orderRefund = (OrderRefund) other;
        return id != null && id.equals(orderRefund.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
