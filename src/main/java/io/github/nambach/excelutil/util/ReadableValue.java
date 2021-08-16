package io.github.nambach.excelutil.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface ReadableValue {
    Object getValue();

    default Optional<String> getString() {
        Object value = getValue();
        if (value instanceof String) {
            return Optional.of((String) value);
        }
        return Optional.empty();
    }

    default Optional<Long> getLong() {
        Object value = getValue();
        if (value instanceof Long) {
            return Optional.of((Long) value);
        }
        return Optional.empty();
    }

    default long getUnsafeLong() {
        return getLong().get();
    }

    default Optional<Integer> getInt() {
        Object value = getValue();
        if (value instanceof Integer) {
            return Optional.of((Integer) value);
        }
        return Optional.empty();
    }

    default int getUnsafeInt() {
        return getInt().get();
    }

    default Optional<Short> getShort() {
        Object value = getValue();
        if (value instanceof Short) {
            return Optional.of((Short) value);
        }
        return Optional.empty();
    }

    default short getUnsafeShort() {
        return getShort().get();
    }

    default Optional<Double> getDouble() {
        Object value = getValue();
        if (value instanceof Double) {
            return Optional.of((Double) value);
        }
        return Optional.empty();
    }

    default double getUnsafeDouble() {
        return getDouble().get();
    }

    default Optional<Float> getFloat() {
        Object value = getValue();
        if (value instanceof Float) {
            return Optional.of((Float) value);
        }
        return Optional.empty();
    }

    default float getUnsafeFloat() {
        return getFloat().get();
    }

    default Optional<Boolean> getBoolean() {
        Object value = getValue();
        if (value instanceof Boolean) {
            return Optional.of((Boolean) value);
        }
        return Optional.empty();
    }

    default boolean getUnsafeBoolean() {
        return getBoolean().get();
    }

    default <T> Optional<T> getAny(Class<T> clazz) {
        return Optional.ofNullable((T) getValue());
    }

    default boolean isNull() {
        return getValue() == null;
    }

    default boolean isNullOrEmpty() {
        Object value = getValue();
        if (value == null) {
            return true;
        }
        if (value instanceof Iterable) {
            return !((Iterable<?>) value).iterator().hasNext();
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        return false;
    }

    default boolean hasValue() {
        return !isNullOrEmpty();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    default Object createCopy() {
        Object value = getValue();

        if (value instanceof Iterable) {
            ArrayList list = new ArrayList<>();
            ((Iterable<?>) value).forEach(list::add);
            return list;
        }

        if (value instanceof Map) {
            return new HashMap<>((Map) value);
        }

        return value;
    }
}
