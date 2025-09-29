package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.result.SpelMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpelExpressionParserTest {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Test
    void parsesSingleBeanReference() {
        var result = parser.parseExpression("@myBean.someMethod() == true");
        assertThat(result.getBeanReferences()).containsExactly("myBean");
        assertThat(result.getMethodReferences().stream().map(SpelMethod::getName)).containsExactly("someMethod");
    }

    @Test
    void parsesMultipleBeanReferences() {
        var result = parser.parseExpression("@beanA.methodA() && @beanB.methodB() && @beanC.methodC() > 0");
        assertThat(result.getBeanReferences()).containsExactlyInAnyOrder("beanA", "beanB", "beanC");
        assertThat(result.getMethodReferences().stream().map(SpelMethod::getName)).containsExactlyInAnyOrder("methodA", "methodB", "methodC");
    }

    @Test
    void parsesNestedMethodReferences() {
        var result = parser.parseExpression("@beanA.outerMethod(@beanB.innerMethod())");
        assertThat(result.getBeanReferences()).containsExactlyInAnyOrder("beanA", "beanB");
        assertThat(result.getMethodReferences().stream().map(SpelMethod::getName)).containsExactlyInAnyOrder("outerMethod", "innerMethod");
    }

    @Test
    void parsesNoReferences() {
        var result = parser.parseExpression("1 + 2");
        assertThat(result.getBeanReferences()).isEmpty();
        assertThat(result.getMethodReferences()).isEmpty();
    }

    @Test
    void parsesInvalidExpressionGracefully() {
        var result = parser.parseExpression("@beanA.methodA('')");
        assertThat(result.getBeanReferences()).contains("beanA");
        assertThat(result.getMethodReferences().stream().map(SpelMethod::getName)).contains("methodA");
    }
}

