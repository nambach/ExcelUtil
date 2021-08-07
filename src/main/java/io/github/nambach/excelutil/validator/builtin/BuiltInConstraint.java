package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BuiltInConstraint {
    public static final Constraint NotNull = new Constraint("not null",
                                                            Objects::nonNull,
                                                            "Value must not be null.");


    public static final Constraint IsString = StringConstraint.IsString;
    public static final Constraint NotEmpty = StringConstraint.NotEmpty;
    public static final Constraint NotBlank = StringConstraint.NotBlank;
    public static final Constraint IsEmail = StringConstraint.IsEmail;
    public static final Function<Long, Constraint> MinLength = StringConstraint.MinLength;
    public static final Function<Long, Constraint> MaxLength = StringConstraint.MaxLength;
    public static final BiFunction<Long, Long, Constraint> BoundLength = StringConstraint.BoundLength;


    public static final Constraint IsInteger = IntegerConstraint.IsInteger;
    public static final Function<Long, Constraint> MinInteger = IntegerConstraint.MinInteger;
    public static final Function<Long, Constraint> MaxInteger = IntegerConstraint.MaxInteger;
    public static final BiFunction<Long, Long, Constraint> BoundInteger = IntegerConstraint.BoundInteger;


    public static final Constraint IsDecimal = DecimalConstraint.IsDecimal;
    public static final Function<Double, Constraint> MinDecimal = DecimalConstraint.MinDecimal;
    public static final Function<Double, Constraint> MaxDecimal = DecimalConstraint.MaxDecimal;
    public static final BiFunction<Double, Double, Constraint> BoundDecimal = DecimalConstraint.BoundDecimal;
}
