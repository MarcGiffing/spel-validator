package com.giffing.spel.validator.assertion;

import com.giffing.spel.validator.core.SpelValidator;
import com.giffing.spel.validator.core.result.SpelScanResult;
import com.giffing.spel.validator.core.result.ValidationItem;
import com.giffing.spel.validator.core.result.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

@Slf4j
public class SpelValidatorAssert extends AbstractAssert<SpelValidatorAssert, List<SpelScanResult>> {

    public SpelValidatorAssert(List<SpelScanResult> actual) {
        super(actual, SpelValidatorAssert.class);
    }

    public static SpelValidatorAssert assertThat(List<SpelScanResult> actual) {
        return new SpelValidatorAssert(actual);
    }


    public SpelValidatorAssert allValid() {
        isNotNull();
        var spelValidator = new SpelValidator(actual);
        var allValid = spelValidator.allMatchStatus(SpelScanResult.Status.VALID);
        if(!allValid) {
            failWithMessage("""
                    ❌ Found invalid SpEL expressions:
                    \t%s
                    """, String.join("\n\t❌\t", actual.stream()
                    .filter(x -> x.getStatus().equals(SpelScanResult.Status.INVALID))
                    .map(SpelValidator::getErrorMessageOfInvalidExpression)
                    .toList()));
        }
        return this;
    }

    public SpelValidatorAssert hasErrors() {
        isNotNull();
        var spelValidator = new SpelValidator(actual);
        var anyInvalid = spelValidator.anyMatchStatus(SpelScanResult.Status.INVALID);
        if(!anyInvalid) {
            failWithMessage("""
                    ❌ No Errors found. Expected any invalid SpEL expressions:
                    \t%s
                    """, String.join("\n\t✅\t", actual.stream()
                    .filter(x -> x.getStatus().equals(SpelScanResult.Status.VALID))
                    .map(SpelValidator::getInfoMessageOfValidExpression)
                    .toList()));
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
        var spelValidator = new SpelValidator(actual);
        var validationResult = spelValidator.usesOnlyMethods(allowedMethods);
        if (validationResult.getStatus().equals(ValidationResult.ValidationStatus.ERROR)) {
            fail(validationResult);
        }

        return this;
    }

    public SpelValidatorAssert usesOnlyBeans(String... allowedBeans) {
        return usesOnlyBeans(List.of(allowedBeans));
    }

    public SpelValidatorAssert usesOnlyBeans(List<String> allowedBeans) {
        isNotNull();
        var spelValidator = new SpelValidator(actual);
        var validationResult = spelValidator.usesOnlyBeans(allowedBeans);
        if (validationResult.getStatus().equals(ValidationResult.ValidationStatus.ERROR)) {
            fail(validationResult);
        }
        return this;
    }

    public SpelValidatorAssert verifyMethodParameter(String methodName, String... allowedParams) {
        return verifyMethodParameter(methodName, List.of(allowedParams));
    }

    public SpelValidatorAssert verifyMethodParameter(String methodName, List<String> allowedParams) {
        var spelValidator = new SpelValidator(actual);
        var validationResult = spelValidator.verifyMethodParameter(methodName, allowedParams);
        if (validationResult.getStatus().equals(ValidationResult.ValidationStatus.ERROR)) {
            fail(validationResult);
        }
        return this;
    }

    private void fail(ValidationResult validationResult) {
        failWithMessage("""
                        ❌ %s
                            %s
                        """, validationResult.getMessage(),
                validationResult.getItems()
                        .stream()
                        .map(ValidationItem::getMessage)
                        .reduce("", (a, b) -> a + "\n\t" + "❌\t" + b));
    }

}
