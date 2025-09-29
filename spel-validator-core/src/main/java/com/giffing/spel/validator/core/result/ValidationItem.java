package com.giffing.spel.validator.core.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationItem {
    private final String message;

    public static ValidationItem of(String message) {
        return new ValidationItem(message);
    }
}
