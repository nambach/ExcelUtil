package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;
import lombok.AccessLevel;
import lombok.Getter;

import static io.github.nambach.excelutil.validator.builtin.IntegerConstraint.BetweenInteger;
import static io.github.nambach.excelutil.validator.builtin.IntegerConstraint.BetweenIntegerExclusive;
import static io.github.nambach.excelutil.validator.builtin.IntegerConstraint.GreaterThanInteger;
import static io.github.nambach.excelutil.validator.builtin.IntegerConstraint.IsInteger;
import static io.github.nambach.excelutil.validator.builtin.IntegerConstraint.LessThanInteger;
import static io.github.nambach.excelutil.validator.builtin.IntegerConstraint.MaxInteger;
import static io.github.nambach.excelutil.validator.builtin.IntegerConstraint.MinInteger;

@Getter(AccessLevel.PACKAGE)
public class IntegerValidator extends TypeValidator {

    IntegerValidator() {
        Constraint.Set temp = new Constraint.Set();
        temp.add(IsInteger);
        temp.addAll(constraints);

        constraints = temp;
    }

    @Override
    public IntegerValidator notNull() {
        super.notNull();
        return this;
    }

    @Override
    public IntegerValidator notNull(String message) {
        super.notNull(message);
        return this;
    }

    public IntegerValidator min(long min) {
        constraints.add(MinInteger.apply(min));
        return this;
    }

    public IntegerValidator min(long min, String message) {
        constraints.add(MinInteger.apply(min).withMessage(message));
        return this;
    }

    public IntegerValidator max(long max) {
        constraints.add(MaxInteger.apply(max));
        return this;
    }

    public IntegerValidator max(long max, String message) {
        constraints.add(MaxInteger.apply(max).withMessage(message));
        return this;
    }

    public IntegerValidator between(long min, long max) {
        constraints.add(BetweenInteger.apply(min, max));
        return this;
    }

    public IntegerValidator between(long min, long max, String message) {
        constraints.add(BetweenInteger.apply(min, max).withMessage(message));
        return this;
    }

    public IntegerValidator greaterThan(long min) {
        constraints.add(GreaterThanInteger.apply(min));
        return this;
    }

    public IntegerValidator greaterThan(long min, String message) {
        constraints.add(GreaterThanInteger.apply(min).withMessage(message));
        return this;
    }

    public IntegerValidator lessThan(long min) {
        constraints.add(LessThanInteger.apply(min));
        return this;
    }

    public IntegerValidator lessThan(long min, String message) {
        constraints.add(LessThanInteger.apply(min).withMessage(message));
        return this;
    }

    public IntegerValidator betweenExclusive(long min, long max) {
        constraints.add(BetweenIntegerExclusive.apply(min, max));
        return this;
    }

    public IntegerValidator betweenExclusive(long min, long max, String message) {
        constraints.add(BetweenIntegerExclusive.apply(min, max).withMessage(message));
        return this;
    }
}
