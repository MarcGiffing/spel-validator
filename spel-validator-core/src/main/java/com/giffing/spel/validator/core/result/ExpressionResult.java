package com.giffing.spel.validator.core.result;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds the result information of parsing a SpEL (Spring Expression Language) expression.
 * <p>
 * This class collects all bean and method references found during the parsing process.
 * <p>
 * Method references are represented by {@link SpelMethod}, which includes the method name and its parameters.
 */
@Data
public class ExpressionResult {
    private Set<String> beanReferences = new HashSet<>();
    /**
     * Set of method references found in the SpEL expression. Each reference includes the method name and its parameters.
     */
    private Set<SpelMethod> methodReferences = new HashSet<>();

    /**
     * Adds a bean reference found in the SpEL expression.
     *
     * @param beanName the name of the bean
     * @return this result object
     */
    public ExpressionResult addBeanReference(String beanName) {
        this.beanReferences.add(beanName);
        return this;
    }

    /**
     * Adds a method reference found in the SpEL expression.
     *
     * @param method the method reference
     * @return this result object
     */
    public ExpressionResult addMethodReference(SpelMethod method) {
        this.methodReferences.add(method);
        return this;
    }
}
