package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

class StringConstraint {
    static final Constraint IsString = new Constraint("is string",
                                                      o -> o == null || o instanceof String,
                                                      "Value must be string.");

    static final Constraint NotEmpty = new Constraint("string not empty",
                                                      o -> o instanceof String && !((String) o).isEmpty(),
                                                      "String must not be empty.");

    static final Constraint NotBlank = new Constraint("string not blank",
                                                      o -> o instanceof String && !((String) o).trim().isEmpty(),
                                                      "String must not be blank.");

    static final Function<Long, Constraint> MinLength =
            minLength -> new Constraint("string min length",
                                        o -> o instanceof String && ((String) o).length() >= minLength,
                                        String.format("Minimum length is %d chars.", minLength));

    static final Function<Long, Constraint> MaxLength =
            maxLength -> new Constraint("string max length",
                                        o -> o instanceof String && ((String) o).length() <= maxLength,
                                        String.format("Maximum length is %d chars.", maxLength));

    static final BiFunction<Long, Long, Constraint> BoundLength =
            (min, max) -> new Constraint("string length boundary",
                                         o -> o instanceof String && ((String) o).length() >= min && ((String) o).length() <= max,
                                         String.format("String's length must be between %d and %d.", min, max));

    static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    static final Constraint IsEmail = new Constraint("string is email",
                                                     o -> o instanceof String && VALID_EMAIL_ADDRESS_REGEX.matcher((String) o).find(),
                                                     "String must be a valid email.");
}
