package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.result.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SpelValidator} covering all public methods.
 */
class SpelValidatorTest {

    /**
     * Tests allMatchStatus and anyMatchStatus methods for valid and invalid cases.
     */
    @Test
    void testMatchStatusMethods() {
        var validResult = new SpelScanResult(SpelScanResult.Status.VALID, null, null, null, null, null);
        var invalidResult = new SpelScanResult(SpelScanResult.Status.INVALID, null, null, null, null, null);
        var validator = new SpelValidator(List.of(validResult, invalidResult));
        assertThat(validator.allMatchStatus(SpelScanResult.Status.VALID)).isFalse();
        assertThat(validator.anyMatchStatus(SpelScanResult.Status.INVALID)).isTrue();
    }

    /**
     * Tests usesOnlyBeans for allowed and not allowed beans.
     */
    @Test
    void testUsesOnlyBeans() {
        var scanResult = TestUtil.createScanResultWithBeans(List.of("bean1", "bean2"));
        var validator = new SpelValidator(List.of(scanResult));
        var resultAllowed = validator.usesOnlyBeans(List.of("bean1", "bean2"));
        assertThat(resultAllowed.getStatus()).isEqualTo(ValidationResult.ValidationStatus.OK);
        var resultNotAllowed = validator.usesOnlyBeans(List.of("bean1"));
        assertThat(resultNotAllowed.getStatus()).isEqualTo(ValidationResult.ValidationStatus.ERROR);
    }

    /**
     * Tests usesOnlyMethods for allowed and not allowed methods.
     */
    @Test
    void testUsesOnlyMethods() {
        var method1 = new SpelMethod("hasRole", List.of());
        var method2 = new SpelMethod("isAdmin", List.of());
        var scanResult = TestUtil.createScanResultWithMethods(List.of(method1, method2));
        var validator = new SpelValidator(List.of(scanResult));
        var resultAllowed = validator.usesOnlyMethods(List.of("hasRole", "isAdmin"));
        assertThat(resultAllowed.getStatus()).isEqualTo(ValidationResult.ValidationStatus.OK);
        var resultNotAllowed = validator.usesOnlyMethods(List.of("hasRole"));
        assertThat(resultNotAllowed.getStatus()).isEqualTo(ValidationResult.ValidationStatus.ERROR);
    }

    /**
     * Tests verifyMethodParameter for allowed and not allowed parameter values.
     */
    @Test
    void testVerifyMethodParameter() {
        var param1 = new SpelMethodParam("'ROLE_USER'");
        var param2 = new SpelMethodParam("'ROLE_ADMIN'");
        var method = new SpelMethod("hasRole", List.of(param1, param2));
        var scanResult = TestUtil.createScanResultWithMethods(List.of(method));
        var validator = new SpelValidator(List.of(scanResult));
        var resultAllowed = validator.verifyMethodParameter("hasRole", List.of("ROLE_USER", "ROLE_ADMIN"));
        assertThat(resultAllowed.getStatus()).isEqualTo(ValidationResult.ValidationStatus.OK);
        var resultNotAllowed = validator.verifyMethodParameter("hasRole", List.of("ROLE_USER"));
        assertThat(resultNotAllowed.getStatus()).isEqualTo(ValidationResult.ValidationStatus.ERROR);
    }

    /**
     * Tests getErrorMessageOfInvalidExpression and getInfoMessageOfValidExpression.
     */
    @Test
    void testErrorAndInfoMessages() {
        var validResult = new SpelScanResult(SpelScanResult.Status.VALID, TestUtil.class, "myMethod", "hasRole('');", "", null);
        var invalidResult = new SpelScanResult(SpelScanResult.Status.INVALID, TestUtil.class, "myMethod", "hasRole('');", "", null);
        assertThat(SpelValidator.getInfoMessageOfValidExpression(validResult)).contains("Expression is valid");
        assertThat(SpelValidator.getErrorMessageOfInvalidExpression(invalidResult)).contains("invalid expressions");
    }

    /**
     * Tests getQuotedValues for correct quoting.
     */
    @Test
    void testGetQuotedValues() {
        var validator = new SpelValidator(Collections.emptyList());
        var quoted = validator.getQuotedValues(List.of("A", "B"));
        assertThat(quoted).containsExactly("'A'", "'B'");
    }

    /**
     * Utility for creating SpELScanResult with beans and methods.
     */
    static class TestUtil {
        static SpelScanResult createScanResultWithBeans(List<String> beans) {
            var exprResult = new ExpressionResult();
            exprResult.setBeanReferences(new HashSet<>(beans));
            exprResult.setMethodReferences(Set.of());
            return new SpelScanResult(SpelScanResult.Status.VALID, TestUtil.class, "myMethod", "hasRole('');", "", exprResult);
        }

        static SpelScanResult createScanResultWithMethods(List<SpelMethod> methods) {
            var exprResult = new ExpressionResult();
            exprResult.setBeanReferences(Set.of());
            exprResult.setMethodReferences(new HashSet<>(methods));
            return new SpelScanResult(SpelScanResult.Status.VALID, TestUtil.class, "myMethod", "hasRole('');", "", exprResult);
        }
    }
}

