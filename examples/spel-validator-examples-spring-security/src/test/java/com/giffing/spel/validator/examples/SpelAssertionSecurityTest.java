package com.giffing.spel.validator.examples;

import com.giffing.spel.validator.assertion.SpelAssertion;
import com.giffing.spel.validator.examples.first.FirstController;
import com.giffing.spel.validator.examples.second.SecondController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpelAssertionSecurityTest {

    @Test
    void validate_first_controller_with_invalid_expression() {
        SpelAssertion.config()
                .registerSecurityDefaults()
                .packageName(FirstController.class.getPackageName())
                .scanSpEL()
                .hasInvalidExpression();
    }

    @Test
    void validate_second_controller_with_correct_roles() {
        SpelAssertion.config()
                .registerSecurityDefaults()
                .packageName(SecondController.class.getPackageName())
                .scanSpEL()
                .hasOnlyValidExpressions()
                .usesOnlyMethods("hasRole")
                .verifyMethodParameter("hasRole", "ROLE_ADMIN", "ROLE_USER");
    }
}


