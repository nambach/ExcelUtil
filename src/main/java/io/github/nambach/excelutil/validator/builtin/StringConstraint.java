package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

class StringConstraint {

    static final Constraint IsString = new Constraint("[String] is string",
                                                      String.class::isInstance,
                                                      "must be a string").nullable();

    static final Constraint NotEmpty = new Constraint("[String] not empty",
                                                      o -> o instanceof String && !((String) o).isEmpty(),
                                                      "must not be empty");

    static final Constraint NotBlank = new Constraint("[String] not blank",
                                                      o -> o instanceof String && !((String) o).trim().isEmpty(),
                                                      "must not be blank");

    static final Function<Long, Constraint> MinLength =
            minLength -> new Constraint("[String] min length",
                                        o -> o instanceof String && ((String) o).length() >= minLength,
                                        String.format("minimum length is %d chars", minLength)).nullable();

    static final Function<Long, Constraint> MaxLength =
            maxLength -> new Constraint("[String] max length",
                                        o -> o instanceof String && ((String) o).length() <= maxLength,
                                        String.format("maximum length is %d chars", maxLength)).nullable();

    static final BiFunction<Long, Long, Constraint> BoundLength =
            (min, max) -> new Constraint("[String] length boundary",
                                         o -> o instanceof String && ((String) o).length() >= min && ((String) o).length() <= max,
                                         String.format("length must be between %d and %d", min, max)).nullable();

    static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    static final Constraint IsEmail = new Constraint("[String] is email",
                                                     o -> o instanceof String && VALID_EMAIL_ADDRESS_REGEX.matcher((String) o).find(),
                                                     "must be a valid email").nullable();

    private StringConstraint() {
    }
}
