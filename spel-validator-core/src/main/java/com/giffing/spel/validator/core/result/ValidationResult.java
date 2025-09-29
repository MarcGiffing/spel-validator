package com.giffing.spel.validator.core.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationResult {
    private final ValidationStatus status;
    private final String message;
    private final List<ValidationItem> items;


    public enum ValidationStatus {
        OK,
        ERROR
    }
}
