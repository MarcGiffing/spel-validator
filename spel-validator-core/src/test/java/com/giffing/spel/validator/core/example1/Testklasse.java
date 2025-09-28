package com.giffing.spel.validator.core.example1;


import org.springframework.stereotype.Service;

@Service
@AliasMeineAnnotation("hasRole('ROLE_ADMIN')")
public class Testklasse {

    @MeineAnnotation("hasRole('B')")
    public void correct() {
    }

    @MeineAnnotation("hasRole('C'))")
    public void invalid_closed_brackets() {
    }

    @MeineAnnotation("hasRole('D') && @unknownBean.someMethod()")
    public void missing_bean() {
    }

    @AliasMeineAnnotation("hasRole('alias_annotation')")
    public void alias_annotation() {
    }

    public void method_without_annotation() {
    }

}
