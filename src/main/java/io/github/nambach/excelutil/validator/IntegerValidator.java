package io.github.nambach.excelutil.validator;

import lombok.AccessLevel;
import lombok.Getter;

import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.BoundInteger;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.IsInteger;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.MaxInteger;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.MinInteger;

@Getter(AccessLevel.PACKAGE)
public class IntegerValidator extends Validator {

    IntegerValidator() {
        constraints.add(IsInteger);
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
        constraints.add(BoundInteger.apply(min, max));
        return this;
    }

    public IntegerValidator between(long min, long max, String message) {
        constraints.add(BoundInteger.apply(min, max).withMessage(message));
        return this;
    }
}
