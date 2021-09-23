package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ReflectUtil;
import io.github.nambach.excelutil.validator.builtin.TypeValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An entity that specifies way to read data from Excel and store into DTO
 *
 * @param <T> DTO
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Handler<T> {

    private Integer colAt;
    private Integer colFrom;
    private String colTitle;
    private String fieldName;
    private BiConsumer<T, ReaderCell> handler;
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

    public Handler<T> validate(Function<TypeValidator, TypeValidator> builder) {
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
        this.handler = ReflectUtil.safeWrap(handler);
        return this;
    }

    boolean needValidation() {
        return typeValidator != null;
    }

    public Handler<T> wrapHandleField(PropertyDescriptor pd) {
        Method setter = pd.getWriteMethod();

        this.handler = (T object, ReaderCell cell) -> {
            Object cellValue;
            switch (ReflectUtil.checkType(pd.getPropertyType())) {
                case STRING:
                    cellValue = cell.readString();
                    break;
                case LONG:
                    cellValue = cell.readLong();
                    break;
                case INTEGER:
                    cellValue = cell.readInt();
                    break;
                case DOUBLE:
                    cellValue = cell.readDouble();
                    break;
                case FLOAT:
                    cellValue = cell.readFloat();
                    break;
                case BOOLEAN:
                    cellValue = cell.readBoolean();
                    break;
                case DATE:
                    cellValue = cell.readDate();
                    break;
                default:
                    cellValue = null;
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
