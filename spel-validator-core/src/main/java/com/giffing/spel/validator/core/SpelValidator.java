package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.result.SpelScanResult;
import com.giffing.spel.validator.core.result.SpelMethod;
import com.giffing.spel.validator.core.result.ValidationItem;
import com.giffing.spel.validator.core.result.ValidationResult;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for SpEL expressions based on scan results.
 */
@RequiredArgsConstructor
public class SpelValidator {
    private final List<SpelScanResult> spelScanResultList;

    public boolean allMatchStatus(SpelScanResult.Status expectedStatus) {
        return spelScanResultList.stream().allMatch(r -> r.getStatus() == expectedStatus);
    }

    public boolean anyMatchStatus(SpelScanResult.Status expectedStatus) {
        return spelScanResultList.stream().anyMatch(r -> r.getStatus() == expectedStatus);
    }

    public ValidationResult usesOnlyBeans(List<String> allowedBeans) {
        List<ValidationItem> messages = new ArrayList<>();
        for (SpelScanResult r : spelScanResultList) {
            switch (r.getStatus()) {
                case VALID -> {
                    var er = r.getExpressionResult();
                    var notAllowed = er.getBeanReferences().stream()
                            .filter(m -> !allowedBeans.contains(m))
                            .toList();
                    if (!notAllowed.isEmpty()) {
                        String message = "%s%s - '%s'".formatted(
                                r.getClazz().getSimpleName(),
                                r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                notAllowed);
                        messages.add(ValidationItem.of(message));
                    }
                }
                case INVALID -> {
                    ValidationItem.of(getErrorMessageOfInvalidExpression(r));
                }
            }
        }
        var resultStatus = ValidationResult.ValidationStatus.OK;
        var resultMessage = "All beans are valid";
        if (!messages.isEmpty()) {
            resultStatus = ValidationResult.ValidationStatus.ERROR;
            resultMessage = "Bean reference not allowed - (allowed:'%s')".formatted(allowedBeans);
        }

        return new ValidationResult(resultStatus, resultMessage, messages);
    }

    public ValidationResult usesOnlyMethods(List<String> allowedMethods) {
        List<ValidationItem> messages = new ArrayList<>();
        for (SpelScanResult r : spelScanResultList) {
            switch (r.getStatus()) {
                case VALID -> {
                    var er = r.getExpressionResult();
                    var notAllowed = er.getMethodReferences().stream()
                            .map(SpelMethod::getName)
                            .filter(m -> !allowedMethods.contains(m))
                            .toList();
                    if (!notAllowed.isEmpty()) {
                        String message = "%s%s - '%s'".formatted(
                                r.getClazz().getSimpleName(),
                                r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                notAllowed);
                        messages.add(ValidationItem.of(message));
                    }
                }
                case INVALID -> {
                    ValidationItem.of(getErrorMessageOfInvalidExpression(r));
                }
            }
        }
        var resultStatus = ValidationResult.ValidationStatus.OK;
        var resultMessage = "All methods are valid";
        if (!messages.isEmpty()) {
            resultStatus = ValidationResult.ValidationStatus.ERROR;
            resultMessage = "Method reference not allowed - (allowed:'%s')".formatted(allowedMethods);
        }

        return new ValidationResult(resultStatus, resultMessage, messages);
    }

    /**
     * Checks if only the given parameter values are used for the specified method reference in the SpEL expressions.
     * Fails if any other parameter values are found for that method.
     *
     * @param methodName    the name of the method to check
     * @param allowedParams list of allowed parameter values (as String)
     * @return this assertion object for method chaining
     */
    public ValidationResult verifyMethodParameter(String methodName, List<String> allowedParams) {
        List<ValidationItem> messages = new ArrayList<>();
        for (SpelScanResult r : spelScanResultList) {
            switch (r.getStatus()) {
                case VALID -> {
                    var er = r.getExpressionResult();
                    er.getMethodReferences().stream()
                            .filter(mr -> methodName.equals(mr.getName()))
                            .forEach(mr -> {
                                for (var param : mr.getParams()) {
                                    if (!getQuotedValues(allowedParams).contains(param.getValue())) {
                                        String errorMessage = "%s%s - Method '%s' uses not allowed parameter value '%s'"
                                                .formatted(r.getClazz().getSimpleName(),
                                                        r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                                        methodName,
                                                        param.getValue());
                                        messages.add(ValidationItem.of(errorMessage));
                                    }
                                }
                            });
                }
                case INVALID -> {
                    ValidationItem.of(getErrorMessageOfInvalidExpression(r));
                }
            }
        }
        var resultStatus = ValidationResult.ValidationStatus.OK;
        var resultMessage = "All method parameters are valid";
        if (!messages.isEmpty()) {
            resultStatus = ValidationResult.ValidationStatus.ERROR;
            resultMessage = "Method '%s' parameter value not allowed - (allowed:'%s')".formatted(methodName, allowedParams);
        }

        return new ValidationResult(resultStatus, resultMessage, messages);
    }

    public static String getErrorMessageOfInvalidExpression(SpelScanResult r) {
        return "%s%s - Cannot perform check because of an invalid expressions. Please fix them first: %s"
                .formatted(r.getClazz().getSimpleName(),
                        r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                        r.getErrorMessage());
    }

    public static String getInfoMessageOfValidExpression(SpelScanResult r) {
        return "%s%s - Expression is valid"
                .formatted(r.getClazz().getSimpleName(),
                        r.getMethod() != null ? "(" + r.getMethod() + ")" : "");
    }

    public List<String> getQuotedValues(List<String> valuesToQuote) {
        return valuesToQuote
                .stream()
                .map(v -> "'" + v + "'")
                .toList();
    }

}
