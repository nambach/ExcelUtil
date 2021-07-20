package io.github.nambach.excelutil.util.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Compare<T> {
    private final List<Criterion<T>> criteria = new ArrayList<>();

    public Compare<T> on(Function<Criterion<T>, Criterion<T>> builder) {
        Objects.requireNonNull(builder);
        Criterion<T> criterion = new Criterion<>();
        builder.apply(criterion);
        criteria.add(criterion);
        return this;
    }

    public List<Criterion<T>> build() {
        return criteria;
    }
}
