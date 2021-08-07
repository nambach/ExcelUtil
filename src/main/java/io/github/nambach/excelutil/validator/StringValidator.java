package io.github.nambach.excelutil.validator;

import lombok.AccessLevel;
import lombok.Getter;

import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.BoundLength;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.IsEmail;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.IsString;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.MaxLength;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.MinLength;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.NotBlank;
import static io.github.nambach.excelutil.validator.builtin.BuiltInConstraint.NotEmpty;

@Getter(AccessLevel.PACKAGE)
public class StringValidator extends Validator {
    StringValidator() {
        constraints.add(IsString);
    }

    @Override
    public StringValidator notNull() {
        super.notNull();
        return this;
    }

    @Override
    public StringValidator notNull(String message) {
        super.notNull(message);
        return this;
    }

    public StringValidator notEmpty() {
        constraints.add(NotEmpty);
        return this;
    }

    public StringValidator notEmpty(String message) {
        constraints.add(NotEmpty.withMessage(message));
        return this;
    }

    public StringValidator notBlank() {
        constraints.add(NotBlank);
        return this;
    }

    public StringValidator notBlank(String message) {
        constraints.add(NotBlank.withMessage(message));
        return this;
    }

    public StringValidator isEmail() {
        constraints.add(IsEmail);
        return this;
    }

    public StringValidator isEmail(String message) {
        constraints.add(IsEmail.withMessage(message));
        return this;
    }

    public StringValidator minLength(long minLength) {
        constraints.add(MinLength.apply(minLength));
        return this;
    }

    public StringValidator minLength(long minLength, String message) {
        constraints.add(MinLength.apply(minLength).withMessage(message));
        return this;
    }

    public StringValidator maxLength(long maxLength) {
        constraints.add(MaxLength.apply(maxLength));
        return this;
    }

    public StringValidator maxLength(long maxLength, String message) {
        constraints.add(MaxLength.apply(maxLength).withMessage(message));
        return this;
    }

    public StringValidator lengthBetween(long min, long max) {
        constraints.add(BoundLength.apply(min, max));
        return this;
    }

    public StringValidator lengthBetween(long min, long max, String message) {
        constraints.add(BoundLength.apply(min, max).withMessage(message));
        return this;
    }
}
