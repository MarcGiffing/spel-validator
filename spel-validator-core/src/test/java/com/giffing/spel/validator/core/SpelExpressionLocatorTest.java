package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.config.AnnotationToScan;
import com.giffing.spel.validator.core.config.SpelConfiguration;
import com.giffing.spel.validator.core.example1.MeineAnnotation;
import com.giffing.spel.validator.core.example1.Testklasse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpelExpressionLocatorTest {

    @Test
    void findsClassesWithMatchingAnnotation() {
        var config = SpelConfiguration.builder()
                .basePackage(Testklasse.class.getPackageName())
                .annotations(List.of(new AnnotationToScan(MeineAnnotation.class.getName())))
                .build();
        var candidates = SpelExpressionLocator.findCandidates(config);
        assertThat(candidates).isNotEmpty();
        assertThat(candidates.stream().anyMatch(c -> c.getName().equals(Testklasse.class.getName()))).isTrue();
    }

    @Test
    void returnsEmptySetForInvalidPackage() {
        var config = SpelConfiguration.builder()
                .basePackage("com.unknown.package")
                .annotations(List.of(new AnnotationToScan(MeineAnnotation.class.getName())))
                .build();
        var candidates = SpelExpressionLocator.findCandidates(config);
        assertThat(candidates).isEmpty();
    }
}

