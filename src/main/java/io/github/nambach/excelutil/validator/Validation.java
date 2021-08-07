package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.util.ListUtil;

import java.util.List;

public class Validation<T> {
    private final Class<T> clazz;
    private final List<Extractor> extractors;

    Validation(Class<T> clazz, List<Extractor> extractors) {
        this.clazz = clazz;
        this.extractors = extractors;
    }

    public static <T> ValidationBuilder<T> fromClass(Class<T> clazz) {
        return new ValidationBuilder<>(clazz);
    }

    public Error validateFull(T object) {
        Error error = new Error(clazz);
        for (Extractor extractor : extractors) {
            Object value = extractor.extract(object);
            List<String> messages = extractor.getValidator().validateFull(value);
            if (ListUtil.hasMember(messages)) {
                error.put(extractor.getFieldName(), messages);
            }
        }
        return error;
    }
}
