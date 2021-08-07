package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.function.BiFunction;
import java.util.function.Function;

class DecimalConstraint {
    static final Constraint IsDecimal = new Constraint("[Decimal] is decimal",
                                                       o -> o == null || o instanceof Float || o instanceof Double,
                                                       "Value must be a decimal.").nullable();

    static final Function<Double, Constraint> MinDecimal =
            min -> new Constraint("[Decimal] min value",
                                  o -> {
                                      if (o instanceof Float) {
                                          return ((Float) o) >= min;
                                      }
                                      if (o instanceof Double) {
                                          return ((Double) o) >= min;
                                      }
                                      return false;
                                  },
                                  String.format("Minimum value is %f.", min)).nullable();

    static final Function<Double, Constraint> MaxDecimal =
            max -> new Constraint("[Decimal] max value",
                                  o -> {
                                      if (o instanceof Float) {
                                          return ((Float) o) <= max;
                                      }
                                      if (o instanceof Double) {
                                          return ((Double) o) <= max;
                                      }
                                      return false;
                                  },
                                  String.format("Maximum value is %f.", max)).nullable();

    static final BiFunction<Double, Double, Constraint> BoundDecimal =
            (min, max) -> new Constraint("[Decimal] boundary",
                                         o -> {
                                             if (o instanceof Float) {
                                                 float val = (Float) o;
                                                 return min <= val && val <= max;
                                             }
                                             if (o instanceof Double) {
                                                 double val = (Double) o;
                                                 return min <= val && val <= max;
                                             }
                                             return false;
                                         },
                                         String.format("Value must be from %f to %f", min, max)).nullable();
}
