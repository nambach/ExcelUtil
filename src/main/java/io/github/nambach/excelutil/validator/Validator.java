package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Validator<T> {
    private final Class<T> clazz;
    private final List<Field<T>> fields;

    Validator(Class<T> clazz, List<Field<T>> fields) {
        this.clazz = clazz;
        this.fields = fields;
    }

    public static <T> Validator<T> fromClass(Class<T> clazz) {
        return new Validator<>(clazz, new ArrayList<>());
    }

    public Validator<T> on(Function<Field<T>, Field<T>> fieldBuilder) {
        Field<T> field = fieldBuilder.apply(new Field<>(clazz));
        if (field.getFieldName() == null) {
            throw new RuntimeException("Field name must be provided.");
        }
        if (field.getExtractor() == null) {
            field.bindField();
        }
        if (field.getTypeValidator() == null) {
            throw new RuntimeException("Validator rule must be provided.");
        }
        this.fields.add(field);
        return this;
    }

    public Error validate(T object) {
        Error error = new Error(clazz);
        for (Field<T> field : fields) {
            Object value = field.extract(object);
            List<String> messages = field.getTypeValidator().test(value);
            if (ListUtil.hasMember(messages)) {
                error.appendError(field.getFieldName(), messages);
            }
        }
        return error;
    }

    public Error.TypeError quickValidate(T object) {
        for (Field<T> field : fields) {
            Object value = field.extract(object);
            String message = field.getTypeValidator().quickTest(value);
            if (message != null) {
                return new Error.TypeError(field.getFieldName(), Collections.singletonList(message));
            }
        }
        return null;
    }
}
