package com.giffing.spel.validator.assertion;

import com.giffing.spel.validator.assertion.usecase_1.MeineAnnotation;
import com.giffing.spel.validator.assertion.usecase_1.Testklasse;
import com.giffing.spel.validator.core.SpELParser;
import com.giffing.spel.validator.core.SpELValidator;
import com.giffing.spel.validator.core.config.AnnotationToScan;
import com.giffing.spel.validator.core.config.SpELConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SpelValidatorAssertTest {

    @Test
    public void test() {
        var spelValidator = new SpELValidator(new SpELParser());

        var result = spelValidator.validateAllExpressions(SpELConfiguration
                .builder()
                .basePackage(Testklasse.class.getPackageName())
                .annotation(AnnotationToScan.of(MeineAnnotation.class))
                .build());
        SpelValidatorAssert.assertThat(result)
                .verifyMethodParameter("hasRole", List.of("B", "C", "D", "alias_class_annotation"))
        ;
    }

}
