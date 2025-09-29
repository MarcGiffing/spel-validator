package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.config.AnnotationToScan;
import com.giffing.spel.validator.core.config.SpelConfiguration;
import com.giffing.spel.validator.core.example1.MeineAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class SpELScannerTest {

    private SpelScanner spelScanner;

    private SpelConfiguration config;

    @BeforeEach
    public void setup() {
        config = SpelConfiguration.builder()
                .basePackage(SpELScannerTest.class.getPackageName())
                .annotations(List.of(new AnnotationToScan(MeineAnnotation.class.getName())))
                .build();
        this.spelScanner = new SpelScanner(new SpelExpressionParser());
    }

    @Test
    public void validateAllSpELExpressions() {
        var results = spelScanner.scan(config);
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