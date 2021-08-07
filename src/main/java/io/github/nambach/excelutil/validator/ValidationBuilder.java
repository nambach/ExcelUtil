package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.validator.builtin.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
public class ValidationBuilder<T> {
    private final Class<T> clazz;
    private final List<Extractor> extractors;
    private Extractor extractor;

    ValidationBuilder(Class<T> clazz) {
        this.clazz = clazz;
        this.extractors = new ArrayList<>();
    }

    @SneakyThrows
    public ValidationBuilder<T> field(String field) {
        if (extractor != null) {
            throw new Exception("Must call .validate() after .field()");
        }
        extractor = new Extractor(field, clazz);
        return this;
    }

    @SneakyThrows
    public ValidationBuilder<T> field(String field, Function<T, ?> extractFn) {
        if (extractor != null) {
            throw new Exception("Must call .validate() after .field()");
        }
        extractor = new Extractor(field, extractFn);
        return this;
    }


    public ValidationBuilder<T> validate(Validator validator) {
        extractor.setValidator(validator);
        extractors.add(extractor);
        extractor = null;
        return this;
    }

    public ValidationBuilder<T> validate(Function<Validator, Validator> builder) {
        Validator validator = builder.apply(Validator.init());
        extractor.setValidator(validator);
        extractors.add(extractor);
        extractor = null;
        return this;
    }

    public Validation<T> build() {
        return new Validation<>(clazz, extractors);
    }
}
