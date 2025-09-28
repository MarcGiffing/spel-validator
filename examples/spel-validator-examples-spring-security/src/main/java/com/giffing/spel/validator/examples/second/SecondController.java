package com.giffing.spel.validator.examples.second;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondController {

    @GetMapping("/second")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String second() {
        return "Hello, World!";
    }
}