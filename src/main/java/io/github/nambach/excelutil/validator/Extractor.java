package io.github.nambach.excelutil.validator;

import io.github.nambach.excelutil.util.ReflectUtil;
import io.github.nambach.excelutil.validator.builtin.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

@Getter
class Extractor {
    private final Function<?, ?> extractor;
    private final String fieldName;

    @Setter(AccessLevel.PACKAGE)
    private Validator validator;

    public Extractor(String fieldName, Function<?, ?> extractor) {
        this.fieldName = fieldName;
        this.extractor = extractor;
    }

    @SneakyThrows
    public Extractor(String fieldName, Class<?> clazz) {
        this.fieldName = fieldName;
        PropertyDescriptor pd = ReflectUtil.getField(fieldName, clazz);
        if (pd != null) {
            extractor = o -> {
                try {
                    return pd.getReadMethod().invoke(o);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            };
        } else {
            throw new Exception(String.format("Could not found field '%s' in class %s", fieldName, clazz.getName()));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object extract(Object object) {
        return ((Function) extractor).apply(object);
    }
}
