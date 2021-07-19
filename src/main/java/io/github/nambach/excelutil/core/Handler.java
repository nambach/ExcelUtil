package io.github.nambach.excelutil.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.function.BiConsumer;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Handler<T> {

    private Integer colAt;
    private Integer colFrom;
    private String fieldName;
    private BiConsumer<T, ReaderCell> handler;

    Handler() {
    }

    Integer getIndex() {
        if (colAt != null) {
            return colAt;
        }
        if (colFrom != null) {
            return colFrom;
        }
        return null;
    }

    public Handler<T> atColumn(int i) {
        colAt = i;
        return this;
    }

    public Handler<T> fromColumn(int i) {
        colFrom = i;
        return this;
    }

    Handler<T> field(String s) {
        fieldName = s;
        return this;
    }

    public Handler<T> handle(BiConsumer<T, ReaderCell> handler) {
        Objects.requireNonNull(handler);
        this.handler = handler;
        return this;
    }
}
