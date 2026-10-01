package com.mentorship.hanakoleh.domain.order.repository;

import com.mentorship.hanakoleh.domain.order.model.Promotion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCodeIgnoreCase(String code);

    @Modifying
    @Query("UPDATE Promotion p SET p.usageCountTotal = p.usageCountTotal + 1, "
            + "p.updatedAt = CURRENT_TIMESTAMP "
            + "WHERE p.id = :promotionId "
            + "AND (p.usageLimitTotal IS NULL OR p.usageCountTotal < p.usageLimitTotal)")
    int incrementUsageCount(@Param("promotionId") Long promotionId);
}
