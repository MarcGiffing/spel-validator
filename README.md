# SpEL Validator

## Motivation

- **Test-based validation of SpEL syntax:**
  Ensure through automated tests that all SpEL expressions used in annotations are syntactically correct. This helps catch errors early and increases reliability.
- **Security rule enforcement:**
  Define rules to restrict which methods and beans can be used in SpEL expressions, improving security and maintainability of your codebase.
- **Annotation support:**
  Works with both standard Spring Security annotations and your own custom annotations containing SpEL expressions.

## Features
- **Static Analysis of SpEL Expressions:** Parses and analyzes SpEL expressions to extract bean references, method calls, and their parameters.
- **Annotation Scanning:** Scans specified packages for custom annotations containing SpEL expressions, supporting both method-level and class-level annotations.
- **Validation Rules:** Allows configuration of allowed beans, methods, and method parameters for SpEL expressions. Supports restriction lists and validation against best practices.
- **AssertJ Integration:** Provides fluent assertion classes for use in tests, enabling expressive validation of SpEL usage in your codebase.

## Usage Example
```java
@Test
public void assertSpEL() {
    SpelAssertion.config()
            .registerSecurityDefaults() // Registers Spring Security Annotations (@PreAuthorize ...)
            .basePackage("com.examples")
            .annotation(MyCustomAnnotation.class) // Specify custom annotation to scan
            .scanSpEL() // Perform the scan
            .hasOnlyValidExpressions() // Verifies all expressions are valid
            .usesOnlyBeans("mySecurityBean") // Restrict to usage of specific beans within SpEL
            .usesOnlyMethods("hasRole") // Restrict to usage of specific methods within SpEL
            .verifyMethodParameter("hasRole", "ROLE_ADMIN", "ROLE_USER", "ROLE_GUEST"); // Restrict parameters for specific method;
}

```

## Requirements
- Java 17+
- Spring Core 6+
- AssertJ (for test assertions)

## License
MIT License
