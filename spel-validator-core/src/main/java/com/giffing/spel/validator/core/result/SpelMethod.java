package com.giffing.spel.validator.core.result;

import lombok.Data;
import java.util.List;

/**
 * Represents a method reference found in a SpEL expression, including its name and parameters.
 */
@Data
public class SpelMethod {
    private final String name;
    private final List<SpelMethodParam> params;
}

