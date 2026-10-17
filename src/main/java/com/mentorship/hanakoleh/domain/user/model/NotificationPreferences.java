package com.mentorship.hanakoleh.domain.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NotificationPreferences {

    @Id
    @Column(name = "customer_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "user_id")
    private Customer customer;

    @Builder.Default
    @Column(name = "email_enabled", nullable = false)
    @NotNull
    private Boolean emailEnabled = true;

    @Builder.Default
    @Column(name = "sms_enabled", nullable = false)
    @NotNull
    private Boolean smsEnabled = true;

    @Builder.Default
    @Column(name = "push_enabled", nullable = false)
    @NotNull
    private Boolean pushEnabled = true;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private Instant updatedAt;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NotificationPreferences)) {
            return false;
        }
        NotificationPreferences notificationPreferences = (NotificationPreferences) other;
        return id != null && id.equals(notificationPreferences.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
