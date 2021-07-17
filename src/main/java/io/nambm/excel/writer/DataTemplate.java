package io.nambm.excel.writer;

import io.nambm.excel.style.DefaultStyle;
import io.nambm.excel.style.Style;
import io.nambm.excel.util.ReflectUtil;
import io.nambm.excel.util.TextUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Sheet;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class DataTemplate<T> implements WriterTemplate {

    private List<ColumnMapper<T>> mappers = new LinkedList<>();
    private Class<T> tClass;

    private boolean reuseForImport;
    private boolean autoResizeColumns;
    private boolean noHeader;

    private Style headerStyle = DefaultStyle.HEADER_STYLE;
    private Style dataStyle = DefaultStyle.DATA_STYLE;
    private Function<T, Style> conditionalRowStyle;

    DataTemplate(Class<T> tClass) {
        this.tClass = tClass;
    }

    public static <T> DataTemplate<T> fromClass(Class<T> tClass) {
        return new DataTemplate<>(tClass);
    }

    public DataTemplate<T> config(Function<Builder<T>, Builder<T>> configBuilder) {
        Objects.requireNonNull(configBuilder);
        Builder<T> cf = new Builder<>(this);
        configBuilder.apply(cf);
        return this;
    }

    public DataTemplate<T> split(Predicate<ColumnMapper<T>> condition) {
        Objects.requireNonNull(condition);
        DataTemplate<T> clone = this.cloneSelf();
        clone.mappers = this.mappers.stream().filter(condition).collect(Collectors.toList());
        return clone;
    }

    public DataTemplate<T> concat(DataTemplate<T> other) {
        if (other == null || other == this) {
            return this;
        }
        DataTemplate<T> clone = this.cloneSelf();
        clone.mappers.addAll(other.mappers);
        return clone;
    }

    private DataTemplate<T> cloneSelf() {
        DataTemplate<T> clone = new DataTemplate<>(tClass);
        clone.mappers.addAll(this.mappers);
        clone.headerStyle = headerStyle;
        clone.dataStyle = dataStyle;
        clone.autoResizeColumns = autoResizeColumns;
        clone.reuseForImport = reuseForImport;
        clone.conditionalRowStyle = conditionalRowStyle;
        return clone;
    }

    Style applyConditionalRowStyle(T object) {
        return ReflectUtil.safeApply(conditionalRowStyle, object);
    }

    public DataTemplate<T> cols(String... fieldNames) {
        List<ColumnMapper<T>> list = Arrays
                .stream(fieldNames)
                .map(s -> new ColumnMapper<T>().field(s))
                .filter(this::validateMapper)
                .collect(Collectors.toList());
        mappers.addAll(list);
        return this;
    }

    public DataTemplate<T> column(Function<ColumnMapper<T>, ColumnMapper<T>> builder) {
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

    public ByteArrayInputStream getFileForImport() {
        DataTemplate<T> clone = this.cloneSelf();
        clone.setReuseForImport(true);
        BaseWriter writer = new BaseWriter();
        Sheet sheet = writer.createNewSheet("Sheet 1");
        writer.writeDataIntoSheet(sheet, clone, null, 0, 0);
        return writer.exportToFile();
    }

    public ByteArrayInputStream writeData(Collection<T> data) {
        BaseWriter writer = new BaseWriter();
        Sheet sheet = writer.createNewSheet("Sheet 1");
        writer.writeDataIntoSheet(sheet, this, data, 0, 0);
        return writer.exportToFile();
    }

    public static class Builder<T> {
        private final DataTemplate<T> config;

        public Builder(DataTemplate<T> config) {
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
