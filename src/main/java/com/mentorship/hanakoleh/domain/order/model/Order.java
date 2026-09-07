package com.mentorship.hanakoleh.domain.order.model;

import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.model.Rider;
import com.mentorship.hanakoleh.domain.user.Customer;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "order_idempotency_key", nullable = false, unique = true)
    @NotNull
    private UUID idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_rider_id")
    private Rider rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_promotion_id")
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_address_id")
    private Address address;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "order_delivery_option", nullable = false, length = 30)
    @NotNull
    private OrderDeliveryOption deliveryOption = OrderDeliveryOption.DELIVERY;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "order_final_status", nullable = false, length = 30)
    @NotNull
    private OrderFinalStatus finalStatus = OrderFinalStatus.CREATED;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "order_payment_status", nullable = false, length = 30)
    @NotNull
    private OrderPaymentStatus paymentStatus = OrderPaymentStatus.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "order_payment_method", nullable = false, length = 30)
    @NotNull
    private OrderPaymentMethod paymentMethod = OrderPaymentMethod.CREDIT_CARD;

    @Builder.Default
    @Column(name = "order_currency_code", nullable = false, length = 3)
    @NotNull
    @Size(min = 3, max = 3)
    private String currencyCode = "EGP";

    @Builder.Default
    @Column(name = "order_subtotal", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "order_delivery_fees", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal deliveryFees = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "order_service_fees", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal serviceFees = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "order_rider_tips", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal riderTips = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "order_discount_amount", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "order_tax_amount", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "order_total_amount", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "order_refunded_amount", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Column(name = "order_delivery_instructions")
    @Size(max = 1000)
    private String deliveryInstructions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "order_delivery_address_snapshot", nullable = false)
    @NotNull
    private Map<String, Object> deliveryAddressSnapshot;

    @Column(name = "order_estimated_delivery_at")
    private OffsetDateTime estimatedDeliveryAt;

    @Column(name = "order_created_at", nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @Column(name = "order_updated_at", nullable = false)
    @NotNull
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    @NotNull
    private Long version = 1L;

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
        if (!(other instanceof Order)) {
            return false;
        }
        Order order = (Order) other;
        return id != null && id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
