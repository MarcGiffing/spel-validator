package com.giffing.spel.validator.core.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Configuration class for SpEL expression validation.
 * <p>
 * This annotation is used to configure the validation process for Spring Expression Language (SpEL) expressions.
 * It specifies the base package to scan, the annotations to look for, and the allowed beans and methods that can be referenced in expressions.
 */
@Getter
@Builder
public class SpelConfiguration {
    /**
     * The base package to scan for annotated classes and methods.
     */
    @NonNull
    private String basePackage;

    /**
     * The list of annotations to scan for SpEL expressions.
     * <p>
     * You can add single annotations via the builder using .annotation(...)
     */
    @NonNull
    @lombok.Singular
    private List<AnnotationToScan> annotations;


}
