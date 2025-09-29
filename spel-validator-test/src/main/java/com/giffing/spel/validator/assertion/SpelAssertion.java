package com.giffing.spel.validator.assertion;

import com.giffing.spel.validator.core.SpelExpressionParser;
import com.giffing.spel.validator.core.SpelScanner;
import com.giffing.spel.validator.core.config.AnnotationToScan;
import com.giffing.spel.validator.core.config.SpelConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpelAssertion {

    public static SpelValidatorBuilder config() {
        return new SpelValidatorBuilder();
    }

    @Slf4j
    public static class SpelValidatorBuilder {
        private String packageName;
        private List<Class<? extends Annotation>> annotations = new ArrayList<>();

        public SpelValidatorBuilder registerSecurityDefaults() {
            getSubclass("org.springframework.security.access.prepost.PreAuthorize").ifPresent(annotations::add);
            getSubclass("org.springframework.security.access.prepost.PostAuthorize").ifPresent(annotations::add);
            getSubclass("org.springframework.security.access.prepost.PostFilter").ifPresent(annotations::add);
            getSubclass("org.springframework.security.access.prepost.PreFilter").ifPresent(annotations::add);
            return this;
        }

        private static Optional<Class<? extends Annotation>> getSubclass(String preAuthorize) {
            try {
                return Optional.of(Class.forName(preAuthorize).asSubclass(Annotation.class));
            } catch (ClassNotFoundException e) {
                log.warn("Spring Security not on classpath, skipping registration of security annotations");

            }
            return Optional.empty();
        }

        public SpelValidatorBuilder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public SpelValidatorBuilder annotations(List<Class<? extends Annotation>> annotations) {
            this.annotations = annotations;
            return this;
        }

        public SpelValidatorAssert scanSpEL() {
            var spelScanner = new SpelScanner(new SpelExpressionParser());

            var results = spelScanner.scan(SpelConfiguration
                    .builder()
                    .basePackage(packageName)
                    .annotations(annotations.stream().map(AnnotationToScan::of).toList())
                    .build());
            return SpelValidatorAssert.assertThat(results);
        }

        public SpelValidatorBuilder annotation(Class<? extends Annotation> annotationToAdd) {
            this.annotations.add(annotationToAdd);
            return this;
        }
    }
}

