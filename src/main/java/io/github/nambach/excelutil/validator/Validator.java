package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.util.ListUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class Validator<T> {
    private final Class<T> clazz;
    private final Map<String, Field<T>> fields;

    Validator(Class<T> clazz) {
        this.clazz = clazz;
        this.fields = new LinkedHashMap<>();
    }

    public static <T> Validator<T> fromClass(Class<T> clazz) {
        return new Validator<>(clazz);
    }

    public Field<T> getField(String fieldName) {
        return fields.get(fieldName);
    }

    public Validator<T> on(UnaryOperator<Field<T>> fieldBuilder) {
        Field<T> field = fieldBuilder.apply(new Field<>(clazz));
        String fieldName = field.getFieldName();
        if (fieldName == null) {
            throw new RuntimeException("Field name must be provided.");
        }
        if (fields.containsKey(fieldName)) {
            throw new RuntimeException(String.format("Field '%s' already existed.", fieldName));
        }
        if (field.getExtractor() == null) {
            field.bindField();
        }
        if (field.getTypeValidator() == null) {
            throw new RuntimeException("Validator rule must be provided.");
        }
        this.fields.put(fieldName, field);
        return this;
    }

    public ObjectError validate(T object) {
        ObjectError objectError = new ObjectError(clazz);
        for (Field<T> field : fields.values()) {
            Object value = field.extract(object);
            List<String> messages = field.getTypeValidator().test(value);
            if (ListUtil.hasMember(messages)) {
                objectError.appendError(field.getFieldName(), messages);
            }
        }
        return objectError;
    }

    public FieldError quickValidate(T object) {
        for (Field<T> field : fields.values()) {
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
