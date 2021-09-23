package io.github.nambach.excelutil.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

class HandlerMap<T> {
    private final Handlers<T> empty = new Handlers<>();

    private final Map<Integer, Handlers<T>> indexAtMap = new HashMap<>();
    private final Map<Integer, Handlers<T>> indexFromMap = new HashMap<>();
    private final Map<String, Handlers<T>> titleMap = new HashMap<>();

    private static <T> void shiftIndexMap(int indexOffset, Map<Integer, Handlers<T>> target) {
        Map<Integer, Handlers<T>> temp = new HashMap<>();
        target.forEach((i, handler) -> temp.put(i + indexOffset, handler));
        target.clear();
        target.putAll(temp);
    }

    private Handlers<T> getIndexFrom(int colIndex) {
        Handlers<T> rs = new Handlers<>();
        for (int i = 0; i <= colIndex; i++) {
            rs.concat(indexFromMap.getOrDefault(i, empty));
        }
        return rs;
    }

    public Handlers<T> get(int colIndex, String colTitle) {
        Handlers<T> indexHandlers = indexAtMap.getOrDefault(colIndex, empty);
        Handlers<T> indexFromHandlers = getIndexFrom(colIndex);
        Handlers<T> titleHandlers = titleMap.getOrDefault(colTitle, empty);

        return new Handlers<T>()
                .concat(indexHandlers)
                .concat(indexFromHandlers)
                .concat(titleHandlers);
    }

    public void putAt(int index, Handler<T> handler) {
        indexAtMap.putIfAbsent(index, new Handlers<>());
        indexAtMap.get(index).add(handler);
    }

    public void putFrom(int index, Handler<T> handler) {
        indexFromMap.putIfAbsent(index, new Handlers<>());
        indexFromMap.get(index).add(handler);
    }

    public void put(String title, Handler<T> handler) {
        titleMap.putIfAbsent(title, new Handlers<>());
        titleMap.get(title).add(handler);
    }

    public HandlerMap<T> makeCopy() {
        HandlerMap<T> clone = new HandlerMap<>();
        clone.indexAtMap.putAll(this.indexAtMap);
        clone.indexFromMap.putAll(this.indexFromMap);
        clone.titleMap.putAll(this.titleMap);
        return clone;
    }

    public void shiftIndexMap(int indexOffset) {
        shiftIndexMap(indexOffset, indexAtMap);
        shiftIndexMap(indexOffset, indexFromMap);
    }

    public int getMinIndex() {
        Integer min1 = indexAtMap.keySet().stream().reduce(Math::min).orElse(null);
        Integer min2 = indexFromMap.keySet().stream().reduce(Math::min).orElse(null);
        return Stream.of(min1, min2)
                     .filter(Objects::nonNull)
                     .reduce(Math::min).orElse(0);
    }

    static class Handlers<T> extends ArrayList<Handler<T>> {

        public Handlers<T> concat(Handlers<T> c) {
            super.addAll(c);
            return this;
        }
    }
}
