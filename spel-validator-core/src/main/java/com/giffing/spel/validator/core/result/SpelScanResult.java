package com.giffing.spel.validator.core.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SpelScanResult {

    private final Status status;
    private final Class<?> clazz;
    private final String method;
    private final String expression;
    private final String errorMessage;
    private final ExpressionResult expressionResult;

    public static SpelScanResult valid(Class<?> clazzName, String method, String expression, ExpressionResult expressionResult) {
        return new SpelScanResult(Status.VALID, clazzName, method, expression, null, expressionResult);
    }

    public static SpelScanResult invalid(Class<?> clazzName, String method, String expression, String errorMessage) {
        return new SpelScanResult(Status.INVALID, clazzName, method, expression, errorMessage, null);
    }

    /**
     * Status of parsing the SpEL expression.
     */
    public enum Status {

        /**
         * The SpEL expression is valid.
         */
        VALID,

        /**
         * The SpEL expression is syntactically invalid.
         */
        INVALID
    }


}
