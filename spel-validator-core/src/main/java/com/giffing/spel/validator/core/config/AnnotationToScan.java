package com.giffing.spel.validator.core.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

/**
 * This class holds information about an annotation that contains a Spring Expression Language (SpEL) expression.
 * <p>
 * The annotation is scanned on both method and class level, and the contained expression is validated.
 * The field {@code attributeName} specifies the attribute of the annotation where the expression is located.
 * <p>
 * The class provides methods to check if the given class name is an annotation and to load the annotation class.
 */
@Data
@RequiredArgsConstructor
public class AnnotationToScan {
    private final String className;
    private String attributeName = "value";

    public static AnnotationToScan of(Class<? extends Annotation> annotation) {
        return new AnnotationToScan(annotation.getName());
    }

    /**
     * Checks if the class name refers to an annotation type.
     *
     * @return true if the class name is an annotation, false otherwise
     */
    public boolean isAnnotation() {
        if (className == null || className.isEmpty()) {
            return false;
        }
        try {
            Class<?> clazz = Class.forName(className);
            return Annotation.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Loads the annotation class for the given class name.
     *
     * @return the annotation class
     * @throws ClassNotFoundException if the class cannot be found
     */
    public Class<? extends Annotation> getAnnotationClass() throws ClassNotFoundException {
        return Class.forName(className).asSubclass(Annotation.class);
    }
}
