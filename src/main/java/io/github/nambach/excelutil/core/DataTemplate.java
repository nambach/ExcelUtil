package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.style.StyleConstant;
import io.github.nambach.excelutil.util.ListUtil;
import io.github.nambach.excelutil.util.ReflectUtil;
import io.github.nambach.excelutil.util.TextUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.ss.util.CellAddress;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A template that hold mapping rules to extract DTO properties and write as Excel table
 *
 * @param <T>
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class DataTemplate<T> {

    private List<ColumnMapper<T>> mappers = new ArrayList<>();
    private Class<T> tClass;

    private int rowAt;
    private int colAt;

    private boolean autoSizeColumns;
    private boolean noHeader;

    private Style headerStyle = StyleConstant.HEADER_STYLE;
    private Style dataStyle = StyleConstant.DATA_STYLE;
    private Function<T, Style> conditionalRowStyle;

    DataTemplate(Class<T> tClass) {
        this.tClass = tClass;
    }

    /**
     * Specify DTO type
     *
     * @param tClass DTO type
     * @param <T>    DTO
     * @return current template
     */
    public static <T> DataTemplate<T> fromClass(Class<T> tClass) {
        return new DataTemplate<>(tClass);
    }

    /**
     * Provide a function to configure data template
     *
     * @param configBuilder a function that builds configuration
     * @return current template
     */
    public DataTemplate<T> config(Function<Builder<T>, Builder<T>> configBuilder) {
        Objects.requireNonNull(configBuilder);
        Builder<T> cf = new Builder<>(this);
        configBuilder.apply(cf);
        return this;
    }

    /**
     * Split the mapping rules to a new template
     *
     * @param condition a {@link Predicate} that filters out mapping rules that need to keep
     * @return a copied template (which is not modified the original)
     */
    public DataTemplate<T> split(Predicate<ColumnMapper<T>> condition) {
        Objects.requireNonNull(condition);
        DataTemplate<T> clone = this.cloneSelf();
        clone.mappers = this.mappers.stream().filter(condition).collect(Collectors.toList());
        return clone;
    }

    /**
     * Combining with other to create a new template that includes all mapping rules of both templates.
     *
     * @param other other {@link DataTemplate}
     * @return a copied template (which is not modified the original)
     */
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
        clone.autoSizeColumns = autoSizeColumns;
        clone.conditionalRowStyle = conditionalRowStyle;
        return clone;
    }

    Style applyConditionalRowStyle(T object) {
        return ReflectUtil.safeApply(conditionalRowStyle, object);
    }

    /**
     * Configure to map all fields of DTO
     *
     * @return current template
     */
    public DataTemplate<T> includeAllFields() {
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            ColumnMapper<T> mapper = new ColumnMapper<T>()
                    .field(field.getName())
                    .title(TextUtil.splitCamelCase(field.getName()));
            mappers.add(mapper);
        }
        return this;
    }

    /**
     * Configure to map some fields of DTO
     *
     * @param fieldNames an array of field names
     * @return current template
     */
    public DataTemplate<T> includeFields(String... fieldNames) {
        List<ColumnMapper<T>> list = Arrays
                .stream(fieldNames)
                .map(s -> new ColumnMapper<T>().field(s))
                .filter(this::validateMapper)
                .filter(mapper -> mappers.stream().noneMatch(current -> Objects.equals(current.getFieldName(), mapper.getFieldName())))
                .collect(Collectors.toList());
        mappers.addAll(list);
        return this;
    }

    /**
     * Filter out some fields of DTO that don't need to export
     *
     * @param fieldNames an array of field names
     * @return current template
     */
    public DataTemplate<T> excludeFields(String... fieldNames) {
        List<String> fields = ListUtil.fromArray(fieldNames);
        mappers = mappers.stream().filter(m -> !fields.contains(m.getField())).collect(Collectors.toList());
        return this;
    }

    /**
     * Configure a {@link ColumnMapper} that define mapping rule to extract DTO data into Excel column
     *
     * @param builder a function that builds {@link ColumnMapper}
     * @return current template
     */
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

    /**
     * Get empty Excel file for import purpose
     *
     * @return an {@link java.io.InputStream} to write as file
     */
    public ByteArrayInputStream getFileForImport() {
        DataTemplate<T> clone = this.cloneSelf();

        Editor editor = new Editor();
        return editor.goToSheet(0)
                     .goToCell(rowAt, colAt)
                     .writeData(clone, null)
                     .exportToFile();
    }

    /**
     * Write data list into Excel table with the current template
     *
     * @param data data list
     * @return an {@link java.io.InputStream} to write as file
     */
    public ByteArrayInputStream writeData(Collection<T> data) {
        Editor editor = new Editor();
        return editor.goToSheet(0)
                     .goToCell(rowAt, colAt)
                     .writeData(this, data)
                     .exportToFile();
    }

    /**
     * Write data list into Excel table with the current template
     *
     * @param sheetName name of the sheet
     * @param data      data list
     * @return an {@link java.io.InputStream} to write as file
     */
    public ByteArrayInputStream writeData(Collection<T> data, String sheetName) {
        Editor editor = new Editor();
        return editor.goToSheet(sheetName)
                     .goToCell(rowAt, colAt)
                     .writeData(this, data)
                     .exportToFile();
    }

    /**
     * Get the {@link ReaderConfig} for reading Excel file that was exported by this template
     *
     * @return a {@link ReaderConfig}
     */
    public ReaderConfig<T> getReaderConfig() {
        ReaderConfig<T> config = ReaderConfig.fromClass(tClass);
        int i = 0;
        for (ColumnMapper<T> mapper : mappers) {
            config.column(i++, mapper.getFieldName());
        }
        config.titleAtRow(0);
        config.dataFromRow(1);
        return config.translate(rowAt, colAt);
    }

    /**
     * Configuration builder for {@link DataTemplate}
     *
     * @param <T>
     */
    public static class Builder<T> {
        private final DataTemplate<T> template;

        public Builder(DataTemplate<T> template) {
            this.template = template;
        }

        /**
         * Specify the beginning cell to write data (includes title row)
         *
         * @param rowAt row index (from 0)
         * @param colAt column index (from 0)
         * @return current builder
         */
        @SneakyThrows
        public Builder<T> startAtCell(int rowAt, int colAt) {
            if (rowAt < 0 || colAt < 0) {
                throw new Exception("Cell coordinate is negative.");
            }
            template.rowAt = rowAt;
            template.colAt = colAt;
            return this;
        }

        /**
         * Specify the beginning cell to write data (includes title row)
         *
         * @param address Excel address (A1, A2...)
         * @return current builder
         */
        @SneakyThrows
        public Builder<T> startAtCell(String address) {
            try {
                CellAddress cellAddress = new CellAddress(address);
                return startAtCell(cellAddress.getRow(), cellAddress.getColumn());
            } catch (Exception e) {
                throw new Exception("Error while parsing cell address: ", e);
            }
        }

        /**
         * Auto resizing all column width
         *
         * @param b boolean
         * @return current builder
         */
        public Builder<T> autoSizeColumns(boolean b) {
            template.autoSizeColumns = b;
            return this;
        }

        /**
         * Hide the title row when write data
         *
         * @param b boolean
         * @return current builder
         */
        public Builder<T> noHeader(boolean b) {
            template.noHeader = b;
            return this;
        }

        /**
         * Set custom style for title row
         *
         * @param style {@link Style}
         * @return current builder
         */
        public Builder<T> headerStyle(Style style) {
            template.headerStyle = style;
            return this;
        }

        /**
         * Set custom style for data section (all data rows)
         *
         * @param style {@link Style}
         * @return current builder
         */
        public Builder<T> dataStyle(Style style) {
            template.dataStyle = style;
            return this;
        }

        /**
         * Set style for a specific data row
         *
         * @param function a function that checks every row data and conditionally return a {@link Style}
         * @return current builder
         */
        public Builder<T> conditionalRowStyle(Function<T, Style> function) {
            Objects.requireNonNull(function);
            template.conditionalRowStyle = function;
            return this;
        }
    }
}
