package io.github.nambach.excelutil.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListUtil {

    public static <T, R> List<R> map(Collection<T> source, Function<? super T, R> mapper) {
        if (isNullOrEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T> List<T> fromArray(T[] array) {
        if (array == null || array.length == 0) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(array));
    }

    public static <T> boolean isNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean hasMember(Collection<T> list) {
        return list != null && !list.isEmpty();
    }

    public static <T> void sort(List<T> list, Comparing<T> comparing) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(comparing);

        Comparator<T> comparator = (o1, o2) -> {
            for (Criterion<T> criterion : comparing.criteria) {
                int compared = criterion.compare(o1, o2);
                if (compared != 0) {
                    return compared;
                }
            }
            return 0;
        };
        list.sort(comparator);
    }

    public static <T> T findIf(Collection<T> collection, Predicate<T> condition) {
        return collection.stream().filter(condition).findFirst().orElse(null);
    }

    public static <T> T findElse(Collection<T> collection, Predicate<T> condition, T defaultValue) {
        T obj = collection.stream().filter(condition).findFirst().orElse(null);
        if (obj == null) {
            collection.add(defaultValue);
            obj = defaultValue;
        }
        return obj;
    }

    public static <K, V> Map<K, List<V>> groupBy(Iterable<V> collection, Function<V, K> keyExtractor) {
        Map<K, List<V>> result = new HashMap<>();
        if (collection == null) {
            return result;
        }
        for (V v : collection) {
            try {
                K key = keyExtractor.apply(v);
                result.putIfAbsent(key, new ArrayList<>());
                result.get(key).add(v);
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}
