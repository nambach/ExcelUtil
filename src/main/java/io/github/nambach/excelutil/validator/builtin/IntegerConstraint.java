package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.function.BiFunction;
import java.util.function.Function;

class IntegerConstraint {
    static final Constraint IsInteger = new Constraint("is integer",
                                                       o -> o == null || o instanceof Integer || o instanceof Long,
                                                       "Value must be integer.");

    static final Function<Long, Constraint> MinInteger =
            min -> new Constraint("integer min value",
                                  o -> {
                                      if (o instanceof Integer) {
                                          return ((Integer) o) >= min;
                                      }
                                      if (o instanceof Long) {
                                          return ((Long) o) >= min;
                                      }
                                      return false;
                                  },
                                  String.format("Minimum value is %d.", min));

    static final Function<Long, Constraint> MaxInteger =
            max -> new Constraint("integer max value",
                                  o -> {
                                      if (o instanceof Integer) {
                                          return ((Integer) o) <= max;
                                      }
                                      if (o instanceof Long) {
                                          return ((Long) o) <= max;
                                      }
                                      return false;
                                  },
                                  String.format("Maximum value is %d.", max));

    static final BiFunction<Long, Long, Constraint> BoundInteger =
            (min, max) -> new Constraint("integer boundary",
                                         o -> {
                                             if (o instanceof Integer) {
                                                 int val = (int) o;
                                                 return min <= val && val <= max;
                                             }
                                             if (o instanceof Long) {
                                                 long val = (long) o;
                                                 return min <= val && val <= max;
                                             }
                                             return false;
                                         },
                                         String.format("Value must be from %d to %d", min, max));
}
