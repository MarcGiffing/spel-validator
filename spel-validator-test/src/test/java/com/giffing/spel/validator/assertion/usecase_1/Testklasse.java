package com.giffing.spel.validator.assertion.usecase_1;


import org.springframework.stereotype.Service;

@Service
@AliasMeineAnnotation("hasRole('alias_class_annotation')")
public class Testklasse {

    @MeineAnnotation("hasRole('B')")
    public void correct() {
    }

    @MeineAnnotation("hasRole('C')")
    public void invalid_closed_brackets() {
    }

    @MeineAnnotation("hasRole('D') && @unknownBean.someMethod()")
    public void missing_bean() {
    }

    public void method_without_annotation() {
    }

}
