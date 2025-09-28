package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.config.AnnotationToScan;
import com.giffing.spel.validator.core.config.SpELConfiguration;
import com.giffing.spel.validator.core.result.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.core.annotation.MergedAnnotations.SearchStrategy.TYPE_HIERARCHY;

@RequiredArgsConstructor
@Slf4j
public class SpELValidator {

    private final SpELParser spelParser;

    /**
     * Validiert alle SpEL-Ausdrücke in den konfigurierten Annotationen.
     */
    @SneakyThrows
    public List<ValidationResult> validateAllExpressions(SpELConfiguration configuration) {
        List<ValidationResult> results = new ArrayList<>();
        var candidates = SpElScanner.findCandidates(configuration);
        for (var candidate : candidates) {
            results.addAll(handleClassAnnotations(configuration, candidate));
            results.addAll(handleMethodsAnnotations(configuration, candidate));
        }
        return results;
    }

    private List<ValidationResult> handleClassAnnotations(SpELConfiguration configuration, Class<?> clazz) {
        List<ValidationResult> results = new ArrayList<>();
        for (var annotationToScan : configuration.getAnnotations()) {
            if (annotationToScan.isAnnotation()) {
                getExpressionFromClass(clazz, annotationToScan)
                        .map(expression -> validateExpression(expression, clazz, null))
                        .ifPresent(results::add);
            }
        }
        return results;
    }

    private List<ValidationResult> handleMethodsAnnotations(SpELConfiguration configuration, Class<?> clazz) {
        List<ValidationResult> results = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            for (var annotationToScan : configuration.getAnnotations()) {
                if (annotationToScan.isAnnotation()) {
                    getExpressionFromMethod(method, annotationToScan)
                            .map(expression ->
                                    validateExpression(expression, clazz, method.getName()))
                            .ifPresent(results::add);
                }
            }
        }
        return results;
    }

    private Optional<String> getExpressionFromClass(Class<?> clazz, AnnotationToScan annotationToScan) {
        try {
            MergedAnnotation<?> mergedAnnotation = MergedAnnotations.from(clazz, TYPE_HIERARCHY)
                    .get(annotationToScan.getAnnotationClass());
            if (mergedAnnotation.isPresent()) {
                return mergedAnnotation.getValue(annotationToScan.getAttributeName(), String.class);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("[SpELValidator] Fehler beim Auslesen der Annotation '{}': {}", annotationToScan.getClassName(), e.getMessage());
            throw new IllegalStateException("SpEl expression couldn't be extracted: " + e.getMessage(), e);
        }
    }

    private Optional<String> getExpressionFromMethod(Method method, AnnotationToScan annotationToScan) {
        try {
            MergedAnnotation<?> mergedAnnotation = MergedAnnotations.from(method, TYPE_HIERARCHY)
                    .get(annotationToScan.getAnnotationClass());
            if (mergedAnnotation.isPresent()) {
                return mergedAnnotation.getValue(annotationToScan.getAttributeName(), String.class);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException("SpEl expression couldn't be extracted: " + e.getMessage(), e);
        }
    }

    private ValidationResult validateExpression(String expressionValue, Class<?> className, String methodName) {
        try {
            var expressionResult = spelParser.parseExpression(expressionValue);
            return ValidationResult.valid(className, methodName, expressionValue, expressionResult);
        } catch (Exception e) {
            return ValidationResult.invalid(className, methodName, expressionValue, e.getMessage());
        }
    }


}
