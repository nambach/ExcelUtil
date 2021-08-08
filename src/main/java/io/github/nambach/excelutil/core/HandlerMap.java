package io.github.nambach.excelutil.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class HandlerMap<T> {
    private final Handlers<T> empty = new Handlers<>();

    private final Map<Integer, Handlers<T>> indexMap = new HashMap<>();
    private final Map<String, Handlers<T>> titleMap = new HashMap<>();

    public Handlers<T> get(int colIndex, String colTitle) {
        Handlers<T> indexHandlers = indexMap.getOrDefault(colIndex, empty);
        Handlers<T> titleHandlers = titleMap.getOrDefault(colTitle, empty);

        return new Handlers<T>()
                .concat(indexHandlers)
                .concat(titleHandlers);
    }

    public void put(int index, Handler<T> handler) {
        indexMap.putIfAbsent(index, new Handlers<>());
        indexMap.get(index).add(handler);
    }

    public void put(String title, Handler<T> handler) {
        titleMap.putIfAbsent(title, new Handlers<>());
        titleMap.get(title).add(handler);
    }

    public HandlerMap<T> cloneSelf() {
        HandlerMap<T> clone = new HandlerMap<>();
        clone.indexMap.putAll(this.indexMap);
        clone.titleMap.putAll(this.titleMap);
        return clone;
    }

    void shiftIndexMap(int indexOffset) {
        Map<Integer, Handlers<T>> temp = new HashMap<>();
        indexMap.forEach((i, handler) -> temp.put(i + indexOffset, handler));
        indexMap.clear();
        indexMap.putAll(temp);
    }

    public int getMinIndex() {
        return indexMap.keySet().stream().reduce(Math::min).orElse(0);
    }

    static class Handlers<T> extends ArrayList<Handler<T>> {

        public Handlers<T> concat(Handlers<T> c) {
            super.addAll(c);
            return this;
        }
    }
}
