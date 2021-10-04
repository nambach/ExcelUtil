package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;
import lombok.AccessLevel;
import lombok.Getter;

import static io.github.nambach.excelutil.validator.builtin.DecimalConstraint.BetweenDecimal;
import static io.github.nambach.excelutil.validator.builtin.DecimalConstraint.BetweenDecimalExclusive;
import static io.github.nambach.excelutil.validator.builtin.DecimalConstraint.GreaterThanDecimal;
import static io.github.nambach.excelutil.validator.builtin.DecimalConstraint.IsDecimal;
import static io.github.nambach.excelutil.validator.builtin.DecimalConstraint.LessThanDecimal;
import static io.github.nambach.excelutil.validator.builtin.DecimalConstraint.MaxDecimal;
import static io.github.nambach.excelutil.validator.builtin.DecimalConstraint.MinDecimal;

@Getter(AccessLevel.PACKAGE)
public class DecimalValidator extends TypeValidator {

    DecimalValidator() {
        Constraint.Set temp = new Constraint.Set();
        temp.add(IsDecimal);
        temp.addAll(constraints);

        constraints = temp;
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
        constraints.add(BetweenDecimal.apply(min, max));
        return this;
    }

    public DecimalValidator between(double min, double max, String message) {
        constraints.add(BetweenDecimal.apply(min, max).withMessage(message));
        return this;
    }

    public DecimalValidator greaterThan(double min) {
        constraints.add(GreaterThanDecimal.apply(min));
        return this;
    }

    public DecimalValidator greaterThan(double min, String message) {
        constraints.add(GreaterThanDecimal.apply(min).withMessage(message));
        return this;
    }

    public DecimalValidator lessThan(double max) {
        constraints.add(LessThanDecimal.apply(max));
        return this;
    }

    public DecimalValidator lessThan(double max, String message) {
        constraints.add(LessThanDecimal.apply(max).withMessage(message));
        return this;
    }

    public DecimalValidator betweenExclusive(double min, double max) {
        constraints.add(BetweenDecimalExclusive.apply(min, max));
        return this;
    }

    public DecimalValidator betweenExclusive(double min, double max, String message) {
        constraints.add(BetweenDecimalExclusive.apply(min, max).withMessage(message));
        return this;
    }
}
