package io.github.nambach.excelutil.validator;

import lombok.AccessLevel;
import lombok.Getter;

import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.BoundDecimal;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.IsDecimal;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.MaxDecimal;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.MinDecimal;

@Getter(AccessLevel.PACKAGE)
public class DecimalValidator extends Validator {

    DecimalValidator() {
        constraints.add(IsDecimal);
    }

    @Override
    public DecimalValidator notNull() {
        super.notNull();
        return this;
    }

    @Override
    public DecimalValidator notNull(String message) {
        super.notNull(message);
        return this;
    }

    public DecimalValidator min(double min) {
        constraints.add(MinDecimal.apply(min));
        return this;
    }

    public DecimalValidator min(double min, String message) {
        constraints.add(MinDecimal.apply(min).withMessage(message));
        return this;
    }

    public DecimalValidator max(double max) {
        constraints.add(MaxDecimal.apply(max));
        return this;
    }

    public DecimalValidator max(double max, String message) {
        constraints.add(MaxDecimal.apply(max).withMessage(message));
        return this;
    }

    public DecimalValidator between(double min, double max) {
        constraints.add(BoundDecimal.apply(min, max));
        return this;
    }

    public DecimalValidator between(double min, double max, String message) {
        constraints.add(BoundDecimal.apply(min, max).withMessage(message));
        return this;
    }
}
