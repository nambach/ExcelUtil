package io.github.nambach.excelutil.util;

import lombok.SneakyThrows;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ReflectUtil {

    private static final Map<String, Map<String, PropertyDescriptor>> CACHED_SCHEME = new HashMap<>();

    private ReflectUtil() {
    }

    /**
     * Double-checked locking (DLC)
     * https://refactoring.guru/design-patterns/singleton/java/example#example-2
     */
    public static <T> PropertyDescriptor getField(String fieldName, Class<T> tClass) {
        String className = tClass.getCanonicalName();

        if (CACHED_SCHEME.containsKey(className)) {
            return CACHED_SCHEME.get(className).get(fieldName);
        }
        Map<String, PropertyDescriptor> map = prepareScheme(tClass);
        return map.get(fieldName);
    }

    private static synchronized <T> Map<String, PropertyDescriptor> prepareScheme(Class<T> tClass) {
        // Fetch from cache
        String className = tClass.getCanonicalName();
        if (CACHED_SCHEME.get(className) != null) {
            return CACHED_SCHEME.get(className);
        }

        // No cache found => create map
        Map<String, PropertyDescriptor> map = new HashMap<>();
        try {
            PropertyDescriptor[] pds = Introspector.getBeanInfo(tClass).getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if (Objects.equals(pd.getName(), "class")) {
                    continue;
                }
                map.put(pd.getName(), pd);
            }
        } catch (IntrospectionException e) {
            System.err.println("Could not initialize property descriptor map.");
            e.printStackTrace();
        }

        // Save cache
        CACHED_SCHEME.put(className, map);
        return map;
    }

    public static <T> void mergeObject(T source, T extra) {
        Class<T> tClass = (Class<T>) source.getClass();

        Map<String, PropertyDescriptor> map = prepareScheme(tClass);
        map.forEach((fieldName, pd) -> {
            try {
                Object value = pd.getReadMethod().invoke(extra);
                if (value == null) {
                    return;
                }

                pd.getWriteMethod().invoke(source, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SneakyThrows
    public static <T> T mergeClone(T source, T extra) {
        Class<T> tClass = (Class<T>) source.getClass();
        T clone = tClass.newInstance();
        mergeObject(clone, source);
        mergeObject(clone, extra);
        return clone;
    }

    public static <O, I> O safeApply(Function<I, O> func, I input) {
        if (func == null) {
            return null;
        }

        try {
            return func.apply(input);
        } catch (Exception e) {
            System.err.println("Error when applying function: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static <T, R> Function<T, R> safeWrap(Function<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                System.err.println("Error while applying function: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        };
    }

    public static <T, U> BiConsumer<T, U> safeWrap(BiConsumer<T, U> consumer) {
        return (t, u) -> {
            try {
                consumer.accept(t, u);
            } catch (Exception e) {
                System.err.println("Error while applying BiConsumer: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    public static ReflectUtil.Type checkType(Class<?> type) {
        if (String.class.equals(type)) {
            return Type.STRING;
        } else if (Long.class.equals(type) || type.getName().equals("long")) {
            return Type.LONG;
        } else if (Integer.class.equals(type) || type.getName().equals("int")) {
            return Type.INTEGER;
        } else if (Double.class.equals(type) || type.getName().equals("double")) {
            return Type.DOUBLE;
        } else if (Float.class.equals(type) || type.getName().equals("float")) {
            return Type.FLOAT;
        } else if (Boolean.class.equals(type) || type.getName().equals("boolean")) {
            return Type.BOOLEAN;
        }
        return Type.OBJECT;
    }

    public enum Type {
        STRING,
        LONG,
        INTEGER,
        DOUBLE,
        FLOAT,
        BOOLEAN,
        DATE,
        OBJECT
    }

}
