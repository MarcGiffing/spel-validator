package com.giffing.spel.validator.assertion.usecase_1;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@MeineAnnotation
public @interface AliasMeineAnnotation {

    @AliasFor(annotation = MeineAnnotation.class, value = "value")
    String value() default "";

}
