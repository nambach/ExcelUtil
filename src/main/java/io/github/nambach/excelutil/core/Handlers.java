package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ReflectUtil;

import java.util.ArrayList;
import java.util.function.BiConsumer;

class Handlers<T> extends ArrayList<Handler<T>> {
    @Override
    public boolean add(Handler<T> el) {
        BiConsumer<T, ReaderCell> safeHandler = ReflectUtil.safeWrap(el.getHandler());
        el.setHandler(safeHandler);
        return super.add(el);
    }
}
