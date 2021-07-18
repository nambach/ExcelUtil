package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.DefaultStyle;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.ReflectUtil;
import io.github.nambach.excelutil.util.TextUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.ss.util.CellAddress;

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

    private int rowAt;
    private int colAt;

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

        Editor editor = new Editor();
        return editor.goToSheet(0)
                     .goToCell(rowAt, colAt)
                     .writeData(clone, null)
                     .exportToFile();
    }

    public ByteArrayInputStream writeData(Collection<T> data) {
        Editor editor = new Editor();
        return editor.goToSheet(0)
                     .goToCell(rowAt, colAt)
                     .writeData(this, data)
                     .exportToFile();
    }

    public ByteArrayInputStream writeData(Collection<T> data, String sheetName) {
        Editor editor = new Editor();
        return editor.goToSheet(sheetName)
                     .goToCell(rowAt, colAt)
                     .writeData(this, data)
                     .exportToFile();
    }

    public ReaderConfig<T> getReaderConfig() {
        ReaderConfig<T> config = ReaderConfig.fromClass(tClass);
        int i = 0;
        for (ColumnMapper<T> mapper : mappers) {
            config.column(i++, mapper.getFieldName());
        }
        if (reuseForImport) {
            config.rowMetadataAt(0);
            config.rowDataFrom(2);
        } else {
            config.rowDataFrom(1);
        }
        return config;
    }

    public static class Builder<T> {
        private final DataTemplate<T> template;

        public Builder(DataTemplate<T> template) {
            this.template = template;
        }

        @SneakyThrows
        public Builder<T> startAtCell(int rowAt, int colAt) {
            if (rowAt < 0 || colAt < 0) {
                throw new Exception("Cell coordinate is negative.");
            }
            template.rowAt = rowAt;
            template.colAt = colAt;
            return this;
        }

        @SneakyThrows
        public Builder<T> startAtCell(String address) {
            try {
                CellAddress cellAddress = new CellAddress(address);
                return startAtCell(cellAddress.getRow(), cellAddress.getColumn());
            } catch (Exception e) {
                throw new Exception("Error while parsing cell address: ", e);
            }
        }

        public Builder<T> reuseForImport(boolean b) {
            template.reuseForImport = b;
            return this;
        }

        public Builder<T> autoResizeColumns(boolean b) {
            template.autoResizeColumns = b;
            return this;
        }

        public Builder<T> noHeader(boolean b) {
            template.noHeader = b;
            return this;
        }

        public Builder<T> headerStyle(Style style) {
            template.headerStyle = style;
            return this;
        }

        public Builder<T> dataStyle(Style style) {
            template.dataStyle = style;
            return this;
        }

        public Builder<T> conditionalRowStyle(Function<T, Style> function) {
            template.conditionalRowStyle = function;
            return this;
        }
    }
}