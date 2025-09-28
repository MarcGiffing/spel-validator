package com.giffing.spel.validator.assertion;

import com.giffing.spel.validator.core.result.SpelMethod;
import com.giffing.spel.validator.core.result.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractAssert;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SpelValidatorAssert extends AbstractAssert<SpelValidatorAssert, List<ValidationResult>> {

    public SpelValidatorAssert(List<ValidationResult> actual) {
        super(actual, SpelValidatorAssert.class);
    }

    public static SpelValidatorAssert assertThat(List<ValidationResult> actual) {
        return new SpelValidatorAssert(actual);
    }


    public SpelValidatorAssert hasOnlyValidExpressions() {
        isNotNull();
        var invalidExpressions = actual.stream()
                .filter(vr -> vr.getStatus() == ValidationResult.Status.INVALID)
                .toList();
        if(!invalidExpressions.isEmpty()) {
            var errorMessages = new ArrayList<String>();
            for(var invalid : invalidExpressions) {
                errorMessages.add("❌ %s%s - %s".formatted(
                        invalid.getClazz().getSimpleName(),
                        invalid.getMethod() != null ? "(" + invalid.getMethod() + ")" : "",
                        invalid.getErrorMessage()
                ));
            }
            failWithMessage("""
                    ❌ Found invalid SpEL expressions:
                    \t%s
                    """, String.join("\n\t", errorMessages));
        }
        return this;
    }

    /**
     * Asserts that there is at least one invalid SpEL expression and outputs all error messages.
     * Fails if no invalid expressions are found.
     *
     * @return this assertion object for method chaining
     */
    public SpelValidatorAssert hasInvalidExpression() {
        isNotNull();
        var invalidExpressions = actual.stream()
                .filter(vr -> vr.getStatus() == ValidationResult.Status.INVALID)
                .toList();
        if (invalidExpressions.isEmpty()) {
            failWithMessage("Expected at least one invalid SpEL expression, but none found.");
        } else {
            var errorMessages = new ArrayList<String>();
            for (var invalid : invalidExpressions) {
                errorMessages.add("❌ %s%s - %s".formatted(
                        invalid.getClazz().getSimpleName(),
                        invalid.getMethod() != null ? "(" + invalid.getMethod() + ")" : "",
                        invalid.getErrorMessage()
                ));
            }
            log.info("""
                    ❌ Found invalid SpEL expressions:
                    	{}
                    """, String.join("\n\t", errorMessages));
        }
        return this;
    }

    public SpelValidatorAssert usesOnlyMethods(String... allowedMethods) {
        return usesOnlyMethods(List.of(allowedMethods));
    }

    /**
     * Checks if only the given methods are used in the SpEL expressions.
     * Fails if any other method references are found.
     *
     * @param allowedMethods List of allowed method names
     * @return this assertion object for method chaining
     */
    public SpelValidatorAssert usesOnlyMethods(List<String> allowedMethods) {
        isNotNull();
        List<String> errorMessages = new ArrayList<>();
        for (ValidationResult r : actual) {
            switch (r.getStatus()) {
                case VALID -> {
                    var er = r.getExpressionResult();
                    var notAllowed = er.getMethodReferences().stream()
                            .map(SpelMethod::getName)
                            .filter(m -> !allowedMethods.contains(m))
                            .toList();
                    if (!notAllowed.isEmpty()) {
                        errorMessages.add("❌ %s%s - '%s'"
                                .formatted(r.getClazz().getSimpleName(),
                                        r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                        notAllowed));
                    }
                }
                case INVALID -> {
                    errorMessages.add("❌ %s%s - Cannot check method references because there are invalid expressions. Please fix them first: %s"
                            .formatted(r.getClazz().getSimpleName(),
                                    r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                    r.getErrorMessage())
                    );
                }
            }
        }
        if (!errorMessages.isEmpty()) {
            failWithMessage("""
                    ❌ Method reference not allowed - (allowed:'%s')
                    \t%s
                    """, allowedMethods, String.join("\n\t", errorMessages));
        }

        return this;
    }

    public SpelValidatorAssert usesOnlyBeans(String... allowedBeans) {
        return usesOnlyBeans(List.of(allowedBeans));
    }

    public SpelValidatorAssert usesOnlyBeans(List<String> allowedBeans) {
        isNotNull();
        List<String> errorMessages = new ArrayList<>();
        for (ValidationResult r : actual) {
            switch (r.getStatus()) {
                case VALID -> {
                    var er = r.getExpressionResult();
                    var notAllowed = er.getBeanReferences().stream()
                            .filter(m -> !allowedBeans.contains(m))
                            .toList();
                    if (!notAllowed.isEmpty()) {
                        errorMessages.add("❌ %s%s - '%s'"
                                .formatted(r.getClazz().getSimpleName(),
                                        r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                        notAllowed));
                    }
                }
                case INVALID -> {
                    errorMessages.add("❌ %s%s - Cannot check bean references because there are invalid expressions. Please fix them first: %s"
                            .formatted(
                                    r.getClazz().getSimpleName(),
                                    r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                    r.getErrorMessage())
                    );
                }
            }
        }
        if (!errorMessages.isEmpty()) {
            failWithMessage("""
                    ❌ Bean reference not allowed - (allowed:'%s')
                    \t%s
                    """, allowedBeans, String.join("\n\t", errorMessages));
        }
        return this;
    }

    public SpelValidatorAssert verifyMethodParameter(String methodName, String... allowedParams) {
        return verifyMethodParameter(methodName, List.of(allowedParams));
    }

    /**
     * Checks if only the given parameter values are used for the specified method reference in the SpEL expressions.
     * Fails if any other parameter values are found for that method.
     *
     * @param methodName the name of the method to check
     * @param allowedParams list of allowed parameter values (as String)
     * @return this assertion object for method chaining
     */
    public SpelValidatorAssert verifyMethodParameter(String methodName, List<String> allowedParams) {
        isNotNull();
        List<String> errorMessages = new ArrayList<>();
        for (ValidationResult r : actual) {
            switch (r.getStatus()) {
                case VALID -> {
                    var er = r.getExpressionResult();
                    er.getMethodReferences().stream()
                        .filter(mr -> methodName.equals(mr.getName()))
                        .forEach(mr -> {
                            for (var param : mr.getParams()) {
                                if (!getQuotedValues(allowedParams).contains(param.getValue())) {
                                    errorMessages.add("❌ %s%s - Method '%s' uses not allowed parameter value '%s'"
                                            .formatted(r.getClazz().getSimpleName(),
                                                    r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                                    methodName,
                                                    param.getValue()));
                                }
                            }
                        });
                }
                case INVALID -> {
                    errorMessages.add("❌ %s%s - Cannot check method parameters because there are invalid expressions. Please fix them first: %s"
                            .formatted(r.getClazz().getSimpleName(),
                                    r.getMethod() != null ? "(" + r.getMethod() + ")" : "",
                                    r.getErrorMessage())
                    );
                }
            }
        }
        if (!errorMessages.isEmpty()) {
            failWithMessage("""
                    ❌ Method '%s' parameter value not allowed - (allowed:'%s')
                    	%s
                    """, methodName, allowedParams, String.join("\n\t", errorMessages));
        }
        return this;
    }

    public List<String> getQuotedValues(List<String> valuesToQuote) {
        return valuesToQuote
                .stream()
                .map(v -> "'" + v + "'")
                .toList();
    }


}
