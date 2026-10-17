package com.mentorship.hanakoleh.domain.checkout.service;

import com.mentorship.hanakoleh.domain.checkout.exception.PromotionNotApplicableException;
import com.mentorship.hanakoleh.domain.checkout.exception.PromotionNotFoundException;
import com.mentorship.hanakoleh.domain.checkout.promotion.PromotionDiscountCalculator;
import com.mentorship.hanakoleh.domain.order.model.Promotion;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.repository.PromotionRepository;
import com.mentorship.hanakoleh.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final OrderRepository orderRepository;
    private final PromotionDiscountCalculator promotionDiscountCalculator;

    @Transactional(readOnly = true)
    public Promotion validate(String code, BigDecimal subtotal, Integer customerId) {
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new PromotionNotFoundException(ErrorCode.PROMOTION_NOT_FOUND.format(code)));

        OffsetDateTime now = OffsetDateTime.now();
        boolean active = Boolean.TRUE.equals(promotion.getIsActive())
                && !now.isBefore(promotion.getStartsAt())
                && !now.isAfter(promotion.getEndsAt());
        if (!active) {
            throw new PromotionNotApplicableException(ErrorCode.PROMOTION_NOT_ACTIVE.format(code));
        }
        if (subtotal.compareTo(promotion.getMinOrderAmount()) < 0) {
            throw new PromotionNotApplicableException(
                    ErrorCode.PROMOTION_BELOW_MIN_ORDER.format(code, promotion.getMinOrderAmount()));
        }
        if (promotion.getUsageLimitTotal() != null
                && promotion.getUsageCountTotal() >= promotion.getUsageLimitTotal()) {
            throw new PromotionNotApplicableException(ErrorCode.PROMOTION_USAGE_EXHAUSTED.format(code));
        }
        long usedByCustomer = orderRepository.countByCustomer_IdAndPromotion_Id(customerId, promotion.getId());
        if (usedByCustomer >= promotion.getUsageLimitPerCustomer()) {
            throw new PromotionNotApplicableException(
                    ErrorCode.PROMOTION_USAGE_PER_CUSTOMER_EXHAUSTED.format(code));
        }
        return promotion;
    }

    public BigDecimal computeDiscount(Promotion promotion, BigDecimal subtotal) {
        return promotionDiscountCalculator.discountFor(promotion, subtotal);
    }

    /** Atomically consume one use; a lost race (limit hit) rolls back the order. */
    public void consume(Promotion promotion) {
        int consumed = promotionRepository.incrementUsageCount(promotion.getId());
        if (consumed == 0) {
            throw new PromotionNotApplicableException(
                    ErrorCode.PROMOTION_USAGE_EXHAUSTED.format(promotion.getCode()));
        }
    }
}
