package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.util.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

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

    public Validator<T> on(UnaryOperator<Field<T>> fieldBuilder) {
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

    public ObjectError validate(T object) {
        ObjectError objectError = new ObjectError(clazz);
        for (Field<T> field : fields) {
            Object value = field.extract(object);
            List<String> messages = field.getTypeValidator().test(value);
            if (ListUtil.hasMember(messages)) {
                objectError.appendError(field.getFieldName(), messages);
            }
        }
        return objectError;
    }

    public FieldError quickValidate(T object) {
        for (Field<T> field : fields) {
            Object value = field.extract(object);
            String message = field.getTypeValidator().quickTest(value);
            if (message != null) {
                FieldError fieldError = new FieldError(field.getFieldName());
                fieldError.append(message);
                return fieldError;
            }
        }
        return null;
    }
}
