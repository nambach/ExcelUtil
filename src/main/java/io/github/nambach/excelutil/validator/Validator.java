package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.util.ListUtil;

import java.util.Collections;
import java.util.List;

public class Validator<T> {
    private final Class<T> clazz;
    private final List<Extractor> extractors;

    Validator(Class<T> clazz, List<Extractor> extractors) {
        this.clazz = clazz;
        this.extractors = extractors;
    }

    public static <T> Builder<T> fromClass(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public Error validate(T object) {
        Error error = new Error(clazz);
        for (Extractor extractor : extractors) {
            Object value = extractor.extract(object);
            List<String> messages = extractor.getTypeValidator().test(value);
            if (ListUtil.hasMember(messages)) {
                error.appendError(extractor.getFieldName(), messages);
            }
        }
        return error;
    }

    public Error.TypeError quickValidate(T object) {
        for (Extractor extractor : extractors) {
            Object value = extractor.extract(object);
            String message = extractor.getTypeValidator().quickTest(value);
            if (message != null) {
                return new Error.TypeError(extractor.getFieldName(), Collections.singletonList(message));
            }
        }
        return null;
    }
}
