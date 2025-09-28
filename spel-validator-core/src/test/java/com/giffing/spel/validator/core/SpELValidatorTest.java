package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.config.AnnotationToScan;
import com.giffing.spel.validator.core.config.SpELConfiguration;
import com.giffing.spel.validator.core.example1.MeineAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class SpELValidatorTest {

    private SpELValidator spelValidator;

    private SpELConfiguration config;

    @BeforeEach
    public void setup() {
        config = SpELConfiguration.builder()
                .basePackage(SpELValidatorTest.class.getPackageName())
                .annotations(List.of(new AnnotationToScan(MeineAnnotation.class.getName())))
                .build();
        this.spelValidator = new SpELValidator(new SpELParser());
    }

    @Test
    public void validateAllSpELExpressions() {
        var results = spelValidator.validateAllExpressions(config);
        for (var r : results) {
            String validMessage = "✅ Valid SpEL-Expression";
            String invalidMessage = "❌ Invalid SpEL-Expression";
            switch (r.getStatus()) {
                case VALID -> log.info("""
                        {}
                        \t Expression: {}
                        \t In class: {}
                        \t In method: {}
                        """, validMessage, r.getExpression(), r.getClazz().getSimpleName(), r.getMethod());
                case INVALID -> log.error("""
                        {}
                        \t Expression: {}
                        \t In class: {}
                        \t In method: {}
                        """,invalidMessage, r.getExpression(), r.getClazz().getSimpleName(), r.getMethod());
            }
        }

    }
}