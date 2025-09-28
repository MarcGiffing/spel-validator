package com.giffing.spel.validator.assertion;

import com.giffing.spel.validator.assertion.usecase_1.MeineAnnotation;
import com.giffing.spel.validator.assertion.usecase_1.Testklasse;
import org.junit.jupiter.api.Test;

class SpelAssertionTest {

    @Test
    public void testSpelAssertion() {
        SpelAssertion.config()
                .registerSecurityDefaults()
                .packageName(Testklasse.class.getPackageName())
                .annotation(MeineAnnotation.class)
                .scanSpEL()
                .hasOnlyValidExpressions();

    }

}