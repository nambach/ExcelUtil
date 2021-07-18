package io.github.nambach.excelutil.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListUtil {

    public static synchronized <T, R> List<R> map(Collection<T> source, Function<? super T, R> mapper) {
        if (isNullOrEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream().map(mapper).collect(Collectors.toList());
    }


    public static synchronized <T> boolean isNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }


}
