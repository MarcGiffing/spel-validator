package com.giffing.spel.validator.core;

import com.giffing.spel.validator.core.result.ExpressionResult;
import com.giffing.spel.validator.core.result.SpelMethod;
import com.giffing.spel.validator.core.result.SpelMethodParam;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.MethodReference;

import java.util.List;

/**
 * Service for parsing and analyzing SpEL expressions.
 */
public class SpelExpressionParser {

    private final org.springframework.expression.spel.standard.SpelExpressionParser parser = new org.springframework.expression.spel.standard.SpelExpressionParser();

    /**
     * Parses the given SpEL expression and returns the result with all bean and method references.
     *
     * @param expressionValue the SpEL expression to parse
     * @return the result containing bean and method references
     */
    public ExpressionResult parseExpression(String expressionValue) {
        var spelExpression = parser.parseRaw(expressionValue);
        return traverseAst(spelExpression.getAST());
    }

    private ExpressionResult traverseAst(SpelNode node) {
        ExpressionResult result = new ExpressionResult();
        if (node instanceof BeanReference b) {
            result.addBeanReference(b.getName());
        }
        if (node instanceof MethodReference m) {

            List<SpelMethodParam> params = new java.util.ArrayList<>();
            for (int i = 0; i < m.getChildCount(); i++) {
                SpelNode argNode = m.getChild(i);
                String value = argNode.toStringAST();
                params.add(new SpelMethodParam(value));
            }
            result.addMethodReference(new SpelMethod(m.getName(), params));
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            var subResult = traverseAst(node.getChild(i));
            result.getBeanReferences().addAll(subResult.getBeanReferences());
            result.getMethodReferences().addAll(subResult.getMethodReferences());
        }
        return result;
    }
}
