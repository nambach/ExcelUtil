package io.nambm.excel.writer;

import io.nambm.excel.style.DefaultStyle;
import io.nambm.excel.style.Style;
import io.nambm.excel.util.ReflectUtil;
import io.nambm.excel.util.TextUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Table<T> {

    private List<ColumnMapper<T>> mappers = new LinkedList<>();
    private Class<T> tClass;

    private boolean reuseForImport;
    private boolean autoResizeColumns;
    private boolean noHeader;
    private Style headerStyle = DefaultStyle.HEADER_STYLE;
    private Style dataStyle = DefaultStyle.DATA_STYLE;
    private Function<T, Style> conditionalRowStyle;

    Table(Class<T> tClass) {
        this.tClass = tClass;
    }

    public static <T> Table<T> fromClass(Class<T> tClass) {
        return new Table<>(tClass);
    }

    public Table<T> config(Function<Builder<T>, Builder<T>> configBuilder) {
        Objects.requireNonNull(configBuilder);
        Builder<T> cf = new Builder<>(this);
        configBuilder.apply(cf);
        return this;
    }

    public Table<T> concat(Table<T> other) {
        if (other == null || other == this) {
            return this;
        }
        Table<T> clone = this.cloneSelf();
        clone.mappers.addAll(other.mappers);
        return clone;
    }

    private Table<T> cloneSelf() {
        Table<T> clone = new Table<>(tClass);
        clone.mappers.addAll(this.mappers);
        clone.headerStyle = headerStyle;
        clone.dataStyle = (dataStyle);
        clone.autoResizeColumns = (autoResizeColumns);
        clone.reuseForImport = (reuseForImport);
        clone.conditionalRowStyle = (conditionalRowStyle);
        return clone;
    }

    Style applyConditionalRowStyle(T object) {
        return ReflectUtil.safeApply(conditionalRowStyle, object);
    }

    public Table<T> cols(String... fieldNames) {
        List<ColumnMapper<T>> list = Arrays
                .stream(fieldNames)
                .map(s -> new ColumnMapper<T>().field(s))
                .filter(this::validateMapper)
                .collect(Collectors.toList());
        mappers.addAll(list);
        return this;
    }

    public Table<T> col(Function<ColumnMapper<T>, ColumnMapper<T>> builder) {
        ColumnMapper<T> mapper = builder.apply(new ColumnMapper<>());
        if (validateMapper(mapper)) {
            mappers.add(mapper);
        }
        return this;
    }

    private boolean validateMapper(ColumnMapper<T> mapper) {
        String fieldName = mapper.getFieldName();
        Function<T, ?> func = mapper.getMapper();
        String title = mapper.getDisplayName();

        if (func != null) {
            if (title == null) {
                mapper.setDisplayName(String.format("Column %d", mappers.size() + 1));
            }
        } else if (fieldName != null) {
            PropertyDescriptor pd = ReflectUtil.getField(fieldName, tClass);
            if (pd == null) {
                return false;
            }
            if (title == null) {
                mapper.setDisplayName(TextUtil.splitCamelCase(fieldName));
            }
        } else {
            return false;
        }
        return true;
    }

    public static class Builder<T> {
        private final Table<T> config;

        public Builder(Table<T> config) {
            this.config = config;
        }

        public Builder<T> reuseForImport(boolean b) {
            config.reuseForImport = b;
            return this;
        }

        public Builder<T> autoResizeColumns(boolean b) {
            config.autoResizeColumns = b;
            return this;
        }

        public Builder<T> noHeader(boolean b) {
            config.noHeader = b;
            return this;
        }

        public Builder<T> headerStyle(Style style) {
            config.headerStyle = style;
            return this;
        }

        public Builder<T> dataStyle(Style style) {
            config.dataStyle = style;
            return this;
        }

        public Builder<T> conditionalRowStyle(Function<T, Style> function) {
            config.conditionalRowStyle = function;
            return this;
        }
    }
}
