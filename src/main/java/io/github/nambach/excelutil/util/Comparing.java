package io.github.nambach.excelutil.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Comparing<T> {
    final List<Criterion<T>> criteria = new ArrayList<>();

    public Comparing<T> thenCompare(Function<Criterion<T>, Criterion<T>> builder) {
        Objects.requireNonNull(builder);
        Criterion<T> criterion = new Criterion<>();
        builder.apply(criterion);
        criteria.add(criterion);
        return this;
    }

    public static <T> Comparing<T> fromClass(Class<T> tClass) {
        return new Comparing<>();
    }
}
