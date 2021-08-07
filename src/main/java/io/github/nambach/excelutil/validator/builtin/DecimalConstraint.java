package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.function.BiFunction;
import java.util.function.Function;

class DecimalConstraint {
    static final Constraint IsDecimal = new Constraint("is decimal",
                                                       o -> o == null || o instanceof Float || o instanceof Double,
                                                       "Value must be a decimal.");

    static final Function<Double, Constraint> MinDecimal =
            min -> new Constraint("decimal min value",
                                  o -> {
                                      if (o instanceof Float) {
                                          return ((Float) o) >= min;
                                      }
                                      if (o instanceof Double) {
                                          return ((Double) o) >= min;
                                      }
                                      return false;
                                  },
                                  String.format("Minimum value is %f.", min));

    static final Function<Double, Constraint> MaxDecimal =
            max -> new Constraint("decimal max value",
                                  o -> {
                                      if (o instanceof Float) {
                                          return ((Float) o) <= max;
                                      }
                                      if (o instanceof Double) {
                                          return ((Double) o) <= max;
                                      }
                                      return false;
                                  },
                                  String.format("Maximum value is %f.", max));

    static final BiFunction<Double, Double, Constraint> BoundDecimal =
            (min, max) -> new Constraint("decimal boundary",
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
                                         String.format("Value must be from %f to %f", min, max));
}
