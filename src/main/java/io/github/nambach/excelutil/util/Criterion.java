package io.github.nambach.excelutil.util;

import lombok.AccessLevel;
import lombok.Getter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
public class Criterion<T> {
    private Function<T, ?> valueExtractor;
    private Direction direction = Direction.ASC;
    private String fieldName;

    Criterion() {
    }

    public Criterion<T> value(Function<T, ?> valueExtractor) {
        Objects.requireNonNull(valueExtractor);
        this.valueExtractor = valueExtractor;
        return this;
    }

    public Criterion<T> field(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public Criterion<T> direction(String direction) {
        if ("desc".equalsIgnoreCase(direction)) {
            this.direction = Direction.DESC;
        }
        return this;
    }

    public Criterion<T> asc() {
        direction = Direction.ASC;
        return this;
    }

    public Criterion<T> desc() {
        direction = Direction.DESC;
        return this;
    }

    private Object extractValue(T object) {
        if (valueExtractor != null) {
            return valueExtractor.apply(object);
        }
        Class<T> tClass = (Class<T>) object.getClass();
        PropertyDescriptor pd = ReflectUtil.getField(fieldName, tClass);
        if (pd != null) {
            try {
                return pd.getReadMethod().invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    int compare(T o1, T o2) {
        int compared;
        Object val1 = extractValue(o1);
        Object val2 = extractValue(o2);
        if (val1 == null && val2 == null) {
            return 0;
        } else if (val1 == null) {
            compared = -1;
        } else if (val2 == null) {
            compared = 1;
        } else {
            compared = internalCompare(val1, val2);
        }
        int direction = this.direction == Direction.ASC ? 1 :
                        this.direction == Direction.DESC ? -1 :
                        0;
        return compared * direction;
    }

    private int internalCompare(Object val1, Object val2) {
        switch (ReflectUtil.determineType(val1)) {
            case STRING:
                return Collator.getInstance(Locale.getDefault()).compare((String) val1, (String) val2);
            case DOUBLE:
                return Double.compare((double) val1, (double) val2);
            case FLOAT:
                return Float.compare((float) val1, (float) val2);
            case LONG:
                return Long.compare((long) val1, (long) val2);
            case INTEGER:
                return Integer.compare((int) val1, (int) val2);
            case BOOLEAN:
                return Boolean.compare((boolean) val1, (boolean) val2);
            case DATE:
                return ((Date) val1).compareTo((Date) val2);
            case OBJECT:
                if (val1 instanceof Comparable) {
                    return ((Comparable) val1).compareTo(val2);
                }
            default:
                return 0;
        }
    }


}