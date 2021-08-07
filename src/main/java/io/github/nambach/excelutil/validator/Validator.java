package io.github.nambach.excelutil.validator;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.NotNull;

@Getter(AccessLevel.PACKAGE)
public class Validator {
    protected Constraint.Set constraints = new Constraint.Set();

    Validator() {
    }

    public static Validator init() {
        return new Validator();
    }

    public static StringValidator string() {
        return new StringValidator();
    }

    public static IntegerValidator integer() {
        return new IntegerValidator();
    }

    public static DecimalValidator decimal() {
        return new DecimalValidator();
    }

    public String validate(Object value) {
        return constraints.stream().filter(constraint -> constraint.notOk(value))
                          .map(Constraint::getMessage)
                          .findFirst().orElse(null);
    }

    public List<String> validateFull(Object value) {
        return constraints.stream().filter(constraint -> constraint.notOk(value))
                          .map(Constraint::getMessage)
                          .collect(Collectors.toList());
    }

    protected void copy(Validator other) {
        this.constraints.addAll(other.constraints);
    }

    public Validator notNull() {
        constraints.add(NotNull);
        return this;
    }

    public Validator notNull(String message) {
        constraints.add(NotNull.withMessage(message));
        return this;
    }

    public StringValidator isString() {
        StringValidator validator = new StringValidator();
        validator.copy(this);
        return validator;
    }

    public IntegerValidator isInteger() {
        IntegerValidator validator = new IntegerValidator();
        validator.copy(this);
        return validator;
    }

    public DecimalValidator isDecimal() {
        DecimalValidator validator = new DecimalValidator();
        validator.copy(this);
        return validator;
    }
}
