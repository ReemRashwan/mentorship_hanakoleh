package com.mentorship.hanakoleh.domain.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "preferences")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Preferences {

    @Id
    @Column(name = "customer_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "user_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", nullable = false, length = 5)
    @Builder.Default
    @NotNull
    private PreferredLanguage preferredLanguage = PreferredLanguage.EN;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_currency", nullable = false, length = 5)
    @Builder.Default
    @NotNull
    private PreferredCurrency preferredCurrency = PreferredCurrency.EGP;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private Instant updatedAt;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Preferences)) {
            return false;
        }
        Preferences preferences = (Preferences) other;
        return id != null && id.equals(preferences.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
