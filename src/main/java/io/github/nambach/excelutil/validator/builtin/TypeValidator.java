package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter(AccessLevel.PACKAGE)
public class TypeValidator {

    static final Constraint NotNull = new Constraint("[Validator] not null",
                                                     Objects::nonNull,
                                                     "must not be null");

    static final ArrayList<Constraint> BasedConstraints = new ArrayList<Constraint>() {{
        add(NotNull);
    }};

    protected Constraint.Set constraints = new Constraint.Set();

    TypeValidator() {
    }

    public static TypeValidator init() {
        return new TypeValidator();
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

    public static TypeValidator custom(Predicate<?> condition, String message) {
        return new TypeValidator().customValidator(condition, message);
    }

    protected boolean containOnlyBased() {
        return constraints.stream().filter(c -> !BasedConstraints.contains(c)).count() == 0;
    }

    public String quickTest(Object value) {
        return constraints.stream()
                          .filter(constraint -> constraint.notOk(value))
                          .map(Constraint::getMessage)
                          .findFirst().orElse(null);
    }

    public List<String> test(Object value) {
        return constraints.stream()
                          .filter(constraint -> constraint.notOk(value))
                          .map(Constraint::getMessage)
                          .collect(Collectors.toList());
    }

    public TypeValidator notNull() {
        constraints.add(NotNull);
        return this;
    }

    public TypeValidator notNull(String message) {
        constraints.add(NotNull.withMessage(message));
        return this;
    }

    protected void copy(TypeValidator other) {
        this.constraints.addAll(other.constraints);
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

    public TypeValidator customValidator(Predicate<?> condition, String message) {
        String name = "[Custom] " + UUID.randomUUID().toString();
        constraints.add(new Constraint(name, condition, message));
        return this;
    }
}
