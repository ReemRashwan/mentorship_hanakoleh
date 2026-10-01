package com.mentorship.hanakoleh.common;

import com.mentorship.hanakoleh.exception.ErrorCode;
import com.mentorship.hanakoleh.exception.InvalidEnumValueException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public final class EnumParser {

    private EnumParser() {
    }

    public static <E extends Enum<E>> E parse(Class<E> type, String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEnumValueException(
                    ErrorCode.INVALID_ENUM_VALUE.format("(empty)", type.getSimpleName(), allowedValues(type)));
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidEnumValueException(
                    ErrorCode.INVALID_ENUM_VALUE.format(value, type.getSimpleName(), allowedValues(type)));
        }
    }

    private static <E extends Enum<E>> String allowedValues(Class<E> type) {
        return Arrays.stream(type.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
