package io.github.nambach.excelutil.validator.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Util {
    static final List<Class<?>> INT = Arrays.asList(Byte.class, Short.class, Integer.class, Long.class);
    static final List<Class<?>> DECIMAL = Arrays.asList(Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class);

    private Util() {
    }

    public static <T> boolean isInstanceOf(Collection<Class<?>> classes, T obj) {
        return classes.stream().anyMatch(aClass -> aClass.isInstance(obj));
    }

    /**
     * Ref: https://stackoverflow.com/a/2683388/11869677
     *
     * @param number an integer
     * @param value  long value
     * @param <T>    any integer type
     * @return compared value
     */
    public static <T> int compareWithLong(T number, long value) {
        return new BigInteger(number.toString()).compareTo(new BigInteger(Long.toString(value)));
    }

    public static <T> int compareWithDouble(T number, double value) {
        return new BigDecimal(number.toString()).compareTo(new BigDecimal(Double.toString(value)));
    }
}
