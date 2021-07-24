package io.github.nambach.excelutil.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * An entity that specifies way to read data from Excel and store into DTO
 *
 * @param <T>
 */
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

    /**
     * Specify the column to handle.
     *
     * @param i column index (from 0)
     * @return current handler
     */
    public Handler<T> atColumn(int i) {
        colAt = i;
        return this;
    }

    /**
     * Specify the beginning column to start handle.
     *
     * @param i start index (from 0)
     * @return current handler
     */
    public Handler<T> fromColumn(int i) {
        colFrom = i;
        return this;
    }

    /**
     * Specify the property of DTO to store the cell value into.
     *
     * @param s DTO field name
     * @return current handler
     */
    Handler<T> field(String s) {
        fieldName = s;
        return this;
    }

    /**
     * Set a custom {@link BiConsumer} to handle storing cell value into DTO.
     *
     * @param handler a {@link BiConsumer} that has 2 parameter, the DTO and the {@link ReaderCell}
     * @return current handler
     */
    public Handler<T> handle(BiConsumer<T, ReaderCell> handler) {
        Objects.requireNonNull(handler);
        this.handler = handler;
        return this;
    }
}
