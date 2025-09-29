package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.config.SpelConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SpelExpressionLocator {

    /**
     * Creates a ClassPathScanningCandidateComponentProvider configured for the given SpELConfiguration.
     * Scans for all configured annotations.
     *
     * @param configuration the SpEL configuration
     * @return the configured scanner
     */
    public static Set<Class<?>> findCandidates(SpelConfiguration configuration) {
        var scanner = new ClassPathScanningCandidateComponentProvider(true);
        configuration.getAnnotations().forEach(annotationToScan -> {
            try {
                if (annotationToScan.isAnnotation()) {
                    scanner.addIncludeFilter(new AnnotationTypeFilter(annotationToScan.getAnnotationClass()));
                } else {
                    log.warn("Is not an annotation: {}", annotationToScan.getClassName());
                }
            } catch (ClassNotFoundException e) {
                log.warn("Configured annotation class not found: {}", annotationToScan.getClassName());
            }
        });
        return scanner.findCandidateComponents(configuration.getBasePackage())
                .stream()
                .map(BeanDefinition::getBeanClassName)
                .map(clazzName -> {
                    try {
                        return Class.forName(clazzName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }
}

