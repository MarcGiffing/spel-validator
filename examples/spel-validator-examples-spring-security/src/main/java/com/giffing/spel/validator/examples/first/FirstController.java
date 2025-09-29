package com.giffing.spel.validator.examples.first;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {

    @GetMapping("/first")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String first() {
        return "Hello, World!";
    }
}