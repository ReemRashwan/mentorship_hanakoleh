package com.mentorship.hanakoleh.domain.cart.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mentorship.hanakoleh.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UpdateCartItemQuantityRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptTheSmallestAllowedQuantity() {
        Set<ConstraintViolation<UpdateCartItemQuantityRequest>> violations = validate(1);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectZeroQuantity() {
        Set<ConstraintViolation<UpdateCartItemQuantityRequest>> violations = validate(0);

        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
        assertEquals(ErrorCode.QUANTITY_MUST_BE_POSITIVE_MESSAGE, violations.iterator().next().getMessage());
    }

    @Test
    void shouldRejectNegativeQuantity() {
        Set<ConstraintViolation<UpdateCartItemQuantityRequest>> violations = validate(-3);

        assertEquals(1, violations.size());
        assertEquals(ErrorCode.QUANTITY_MUST_BE_POSITIVE_MESSAGE, violations.iterator().next().getMessage());
    }

    @Test
    void shouldRejectMissingQuantity() {
        Set<ConstraintViolation<UpdateCartItemQuantityRequest>> violations = validate(null);

        assertEquals(1, violations.size());
        assertEquals("quantity", violations.iterator().next().getPropertyPath().toString());
        assertEquals(ErrorCode.QUANTITY_REQUIRED_MESSAGE, violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<UpdateCartItemQuantityRequest>> validate(Integer quantity) {
        UpdateCartItemQuantityRequest request = new UpdateCartItemQuantityRequest();
        request.setQuantity(quantity);

        return validator.validate(request);
    }
}
