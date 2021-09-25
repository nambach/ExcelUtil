package io.github.nambach.excelutil.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Comparing<T> {
    final List<Criterion<T>> criteria = new ArrayList<>();

    public static <T> Comparing<T> fromClass(Class<T> tClass) {
        return new Comparing<>();
    }

    public Comparing<T> thenCompare(UnaryOperator<Criterion<T>> builder) {
        Objects.requireNonNull(builder);
        Criterion<T> criterion = new Criterion<>();
        builder.apply(criterion);
        criteria.add(criterion);
        return this;
    }

    public Comparing<T> thenCompareValue(Function<T, ?> valueExtractor) {
        Objects.requireNonNull(valueExtractor);
        Criterion<T> criterion = new Criterion<>();
        criterion.value(valueExtractor);
        criteria.add(criterion);
        return this;
    }

    public Comparing<T> thenCompareField(String fieldName) {
        Objects.requireNonNull(fieldName);
        Criterion<T> criterion = new Criterion<>();
        criterion.field(fieldName);
        criteria.add(criterion);
        return this;
    }
}
