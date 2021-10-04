package io.github.nambach.excelutil.validator.builtin;

import io.github.nambach.excelutil.validator.Constraint;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.github.nambach.excelutil.validator.builtin.Util.INT;
import static io.github.nambach.excelutil.validator.builtin.Util.compareWithLong;
import static io.github.nambach.excelutil.validator.builtin.Util.isInstanceOf;

class IntegerConstraint {

    static final Constraint IsInteger =
            new Constraint("[Integer] is integer",
                           o -> isInstanceOf(INT, o),
                           "must be an integer").nullable();

    static final Function<Long, Constraint> MinInteger =
            min -> new Constraint("[Integer] min value",
                                  o -> {
                                      if (isInstanceOf(INT, o)) {
                                          return compareWithLong(o, min) >= 0;
                                      }
                                      return false;
                                  },
                                  String.format("minimum value is %d", min)).nullable();

    static final Function<Long, Constraint> MaxInteger =
            max -> new Constraint("[Integer] max value",
                                  o -> {
                                      if (isInstanceOf(INT, o)) {
                                          return compareWithLong(o, max) <= 0;
                                      }
                                      return false;
                                  },
                                  String.format("maximum value is %d", max)).nullable();

    static final BiFunction<Long, Long, Constraint> BetweenInteger =
            (min, max) -> new Constraint("[Integer] boundary",
                                         o -> {
                                             if (isInstanceOf(INT, o)) {
                                                 return compareWithLong(o, min) >= 0 && compareWithLong(o, max) <= 0;
                                             }
                                             return false;
                                         },
                                         String.format("must be from %d to %d", min, max)).nullable();

    static final Function<Long, Constraint> GreaterThanInteger =
            min -> new Constraint("[Integer] greater than value",
                                  o -> {
                                      if (isInstanceOf(INT, o)) {
                                          return compareWithLong(o, min) > 0;
                                      }
                                      return false;
                                  },
                                  String.format("value must be greater than %d", min)).nullable();

    static final Function<Long, Constraint> LessThanInteger =
            max -> new Constraint("[Integer] less than value",
                                  o -> {
                                      if (isInstanceOf(INT, o)) {
                                          return compareWithLong(o, max) < 0;
                                      }
                                      return false;
                                  },
                                  String.format("value must be less than %d", max)).nullable();

    static final BiFunction<Long, Long, Constraint> BetweenIntegerExclusive =
            (min, max) -> new Constraint("[Integer] exclusive boundary",
                                         o -> {
                                             if (isInstanceOf(INT, o)) {
                                                 return compareWithLong(o, min) > 0 && compareWithLong(o, max) < 0;
                                             }
                                             return false;
                                         },
                                         String.format("value must be greater than %d and less than %d", min, max)).nullable();

    private IntegerConstraint() {
    }
}
