package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.validator.builtin.TypeValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
public class Builder<T> {
    private final Class<T> clazz;
    private final List<Extractor> extractors;
    private Extractor extractor;

    Builder(Class<T> clazz) {
        this.clazz = clazz;
        this.extractors = new ArrayList<>();
    }

    @SneakyThrows
    public Builder<T> field(String field) {
        if (extractor != null) {
            throw new Exception("Must call .validate() after .field()");
        }
        extractor = new Extractor(field, clazz);
        return this;
    }

    @SneakyThrows
    public Builder<T> field(String field, Function<T, ?> extractFn) {
        if (extractor != null) {
            throw new Exception("Must call .validate() after .field()");
        }
        extractor = new Extractor(field, extractFn);
        return this;
    }


    public Builder<T> validate(TypeValidator typeValidator) {
        extractor.setTypeValidator(typeValidator);
        extractors.add(extractor);
        extractor = null;
        return this;
    }

    public Builder<T> validate(Function<TypeValidator, TypeValidator> builder) {
        TypeValidator typeValidator = builder.apply(TypeValidator.init());
        extractor.setTypeValidator(typeValidator);
        extractors.add(extractor);
        extractor = null;
        return this;
    }

    public Validator<T> build() {
        return new Validator<>(clazz, extractors);
    }
}
