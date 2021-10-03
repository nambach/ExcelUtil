package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ReflectUtil;
import io.github.nambach.excelutil.validator.builtin.TypeValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.var;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * An entity that specifies way to read data from Excel and store into DTO
 *
 * @param <T> DTO
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Handler<T> {

    private static final Map<Class<?>, Function<ReaderCell, Object>> FIELD_READERS = new HashMap<>();

    static {
        var o = FIELD_READERS;
        o.put(String.class, ReaderCell::readString);
        o.put(Long.class, ReaderCell::readLong);
        o.put(long.class, ReaderCell::readLong);
        o.put(Integer.class, ReaderCell::readInt);
        o.put(int.class, ReaderCell::readInt);
        o.put(Double.class, ReaderCell::readDouble);
        o.put(double.class, ReaderCell::readDouble);
        o.put(Float.class, ReaderCell::readFloat);
        o.put(float.class, ReaderCell::readFloat);
        o.put(Boolean.class, ReaderCell::readBoolean);
        o.put(boolean.class, ReaderCell::readBoolean);
        o.put(Date.class, ReaderCell::readDate);
    }

    private Integer colAt;
    private Integer colFrom;
    private String colTitle;
    private String fieldName;
    private BiConsumer<T, ReaderCell> coreHandler;
    private TypeValidator typeValidator;

    Handler() {
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

    public Handler<T> atColumn(String colTitle) {
        this.colTitle = colTitle;
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

    public Handler<T> validate(TypeValidator typeValidator) {
        this.typeValidator = typeValidator;
        return this;
    }

    public Handler<T> validate(UnaryOperator<TypeValidator> builder) {
        this.typeValidator = builder.apply(TypeValidator.init());
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
        this.coreHandler = ReflectUtil.safeWrap(handler);
        return this;
    }

    boolean needValidation() {
        return typeValidator != null;
    }

    protected Handler<T> wrapHandleField(PropertyDescriptor pd) {
        Method setter = pd.getWriteMethod();
        Class<?> type = pd.getPropertyType();

        this.coreHandler = (T object, ReaderCell cell) -> {
            Object cellValue;
            Function<ReaderCell, Object> reader = FIELD_READERS.get(type);
            if (reader == null) {
                cellValue = null;
            } else {
                cellValue = reader.apply(cell);
            }

            try {
                setter.invoke(object, cellValue);
            } catch (Exception e) {
                System.err.println("Error while invoking setter: " + e.getMessage());
            }
        };

        return this;
    }
}
