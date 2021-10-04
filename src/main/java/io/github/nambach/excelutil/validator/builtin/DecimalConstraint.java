package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.nambach.excelutil.validator.builtin.Util.DECIMAL;
import static io.github.nambach.excelutil.validator.builtin.Util.compareWithDouble;
import static io.github.nambach.excelutil.validator.builtin.Util.isInstanceOf;

class DecimalConstraint {
    static final Constraint IsDecimal = new Constraint("[Decimal] is decimal",
                                                       o -> isInstanceOf(DECIMAL, o),
                                                       "must be a decimal").nullable();

    static final Function<Double, Constraint> MinDecimal =
            min -> new Constraint("[Decimal] min value",
                                  o -> {
                                      if (isInstanceOf(DECIMAL, o)) {
                                          return compareWithDouble(o, min) >= 0;
                                      }
                                      return false;
                                  },
                                  String.format("minimum value is %f", min)).nullable();

    static final Function<Double, Constraint> MaxDecimal =
            max -> new Constraint("[Decimal] max value",
                                  o -> {
                                      if (isInstanceOf(DECIMAL, o)) {
                                          return compareWithDouble(o, max) <= 0;
                                      }
                                      return false;
                                  },
                                  String.format("maximum value is %f", max)).nullable();

    static final BiFunction<Double, Double, Constraint> BetweenDecimal =
            (min, max) -> new Constraint("[Decimal] inclusively between",
                                         o -> {
                                             if (isInstanceOf(DECIMAL, o)) {
                                                 return compareWithDouble(o, min) >= 0 && compareWithDouble(o, max) <= 0;
                                             }
                                             return false;
                                         },
                                         String.format("value must be from %f to %f", min, max)).nullable();

    static final Function<Double, Constraint> GreaterThanDecimal =
            min -> new Constraint("[Decimal] greater than value",
                                  o -> {
                                      if (isInstanceOf(DECIMAL, o)) {
                                          return compareWithDouble(o, min) > 0;
                                      }
                                      return false;
                                  },
                                  String.format("value must be greater than %f", min)).nullable();

    static final Function<Double, Constraint> LessThanDecimal =
            max -> new Constraint("[Decimal] less than value",
                                  o -> {
                                      if (isInstanceOf(DECIMAL, o)) {
                                          return compareWithDouble(o, max) < 0;
                                      }
                                      return false;
                                  },
                                  String.format("value must be less than %f", max)).nullable();

    static final BiFunction<Double, Double, Constraint> BetweenDecimalExclusive =
            (min, max) -> new Constraint("[Decimal] exclusively between",
                                         o -> {
                                             if (isInstanceOf(DECIMAL, o)) {
                                                 return compareWithDouble(o, min) > 0 && compareWithDouble(o, max) < 0;
                                             }
                                             return false;
                                         },
                                         String.format("value must be greater than %f and less than %f", min, max)).nullable();
}
