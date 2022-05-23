package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.style.StyleConstant;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.util.CellAddress;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A template that contains mapping rules to extract
 * DTO properties and write as Excel table.
 *
 * @param <T> DTO
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@Log4j2
public class DataTemplate<T> extends ColumnTemplate<T> {

    private int rowAt;
    private int colAt;

    private boolean autoSizeColumns;
    private boolean noHeader;

    private transient Style headerStyle = StyleConstant.HEADER_STYLE;
    private transient Style dataStyle = StyleConstant.DATA_STYLE;

    private transient Function<T, Style> conditionalRowStyle;

    DataTemplate(Class<T> tClass) {
        super(tClass);
    }

    /**
     * Specify the DTO type.
     *
     * @param tClass DTO type
     * @param <T>    DTO
     * @return current template
     */
    public static <T> DataTemplate<T> fromClass(Class<T> tClass) {
        return new DataTemplate<>(tClass);
    }

    /**
     * Provide a function to configure data template.
     *
     * @param configBuilder a function that builds configuration
     * @return current template
     */
    public DataTemplate<T> config(UnaryOperator<Builder<T>> configBuilder) {
        Objects.requireNonNull(configBuilder);
        Builder<T> cf = new Builder<>(this);
        configBuilder.apply(cf);
        return this;
    }

    /**
     * Split the mapping rules to a new template.
     *
     * @param condition a {@link Predicate} that filters out mapping rules that need to keep
     * @return a copied template (which is not modified the original)
     */
    @Override
    public DataTemplate<T> split(Predicate<ColumnMapper<T>> condition) {
        DataTemplate<T> clone = this.makeCopy();
        this.internalSplit(clone, condition);
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
        DataTemplate<T> clone = this.makeCopy();
        clone.addAll(other);
        return clone;
    }

    /**
     * Configure to map some fields of DTO.
     *
     * @param fieldNames an array of field names
     * @return current template
     */
    @Override
    public DataTemplate<T> includeFields(String... fieldNames) {
        super.includeFields(fieldNames);
        return this;
    }

    /**
     * Configure to map all fields of DTO.
     *
     * @return current template
     */
    @Override
    public DataTemplate<T> includeAllFields() {
        super.includeAllFields();
        return this;
    }

    /**
     * Filter out some fields of DTO that don't need to export.
     *
     * @param fieldNames an array of field names
     * @return current template
     */
    @Override
    public DataTemplate<T> excludeFields(String... fieldNames) {
        super.excludeFields(fieldNames);
        return this;
    }

    /**
     * Configure a {@link ColumnMapper} that define mapping rule to extract DTO data into Excel column.
     *
     * @param builder a function that builds {@link ColumnMapper}
     * @return current template
     */
    public DataTemplate<T> column(UnaryOperator<ColumnMapper<T>> builder) {
        super.column(builder);
        return this;
    }

    public DataTemplate<T> makeCopy() {
        DataTemplate<T> clone = new DataTemplate<>(tClass);
        clone.addAll(this);
        clone.copyConfig(this);
        clone.conditionalRowStyle = conditionalRowStyle;
        return clone;
    }

    private void copyConfig(DataTemplate<?> other) {
        rowAt = other.rowAt;
        colAt = other.colAt;

        autoSizeColumns = other.autoSizeColumns;
        noHeader = other.noHeader;

        headerStyle = other.headerStyle;
        dataStyle = other.dataStyle;
    }

    Style applyConditionalRowStyle(T object) {
        if (conditionalRowStyle == null) {
            return null;
        }
        return conditionalRowStyle.apply(object);
    }

    /**
     * Get empty Excel file for import purpose.
     *
     * @return an {@link java.io.InputStream} to write as file
     */
    public ByteArrayInputStream getFileForImport() {
        try (Editor editor = new Editor()) {
            DataTemplate<T> clone = this.makeCopy();
            return editor.goToSheet(0)
                         .goToCell(rowAt, colAt)
                         .writeData(clone, null)
                         .exportToFile();
        }
    }

    /**
     * Write data list into Excel table with the current template.
     *
     * @param data data list
     * @return an {@link java.io.InputStream} to write as file
     */
    public ByteArrayInputStream writeData(Collection<T> data) {
        try (Editor editor = new Editor()) {
            return editor.goToSheet(0)
                         .goToCell(rowAt, colAt)
                         .writeData(this, data)
                         .exportToFile();
        }
    }

    /**
     * Write data list into Excel table with the current template.
     *
     * @param sheetName name of the sheet
     * @param data      data list
     * @return an {@link java.io.InputStream} to write as file
     */
    public ByteArrayInputStream writeData(Collection<T> data, String sheetName) {
        try (Editor editor = new Editor()) {
            return editor.goToSheet(sheetName)
                         .goToCell(rowAt, colAt)
                         .writeData(this, data)
                         .exportToFile();
        }
    }

    /**
     * Get the {@link ReaderConfig} for reading Excel file that was exported by this template.
     *
     * @return a {@link ReaderConfig}
     */
    public ReaderConfig<T> getReaderConfigByColumnIndex() {
        ReaderConfig<T> config = ReaderConfig.fromClass(tClass);
        int i = 0;
        for (ColumnMapper<T> mapper : this) {
            if (mapper.getFieldName() != null) {
                config.column(i++, mapper.getFieldName());
            }
        }
        if (noHeader) {
            config.dataFromRow(0);
        } else {
            config.titleAtRow(0);
            config.dataFromRow(1);
        }
        return config.translate(rowAt, colAt);
    }

    /**
     * Get the {@link ReaderConfig} for reading Excel file that was exported by this template.
     *
     * @return a {@link ReaderConfig}
     */
    @SneakyThrows
    public ReaderConfig<T> getReaderConfig() {
        if (noHeader) {
            log.error("WARNING: Source file must include header row; 'noHeader=true' found.");
        }
        ReaderConfig<T> config = ReaderConfig.fromClass(tClass);
        config.titleAtRow(rowAt);
        config.dataFromRow(rowAt + 1);
        for (ColumnMapper<T> mapper : this) {
            if (mapper.getTitle() == null) {
                throw new RuntimeException("Title of field '" + mapper.getFieldName() + "' must be provided.");
            }
            if (mapper.getFieldName() != null) {
                config.column(mapper.getTitle(), mapper.getFieldName());
            }
        }
        return config;
    }

    // Flatten Section
    @SuppressWarnings({"unchecked"})
    DataTemplate<FlatData> getFlatTemplate() {
        DataTemplate<FlatData> result = new DataTemplate<>(FlatData.class);
        result.copyConfig(this);

        for (ColumnMapper<T> mapper : this) {
            if (mapper.isListField()) {
                collectDeepMapper(mapper, 1, result);
            } else {
                Function<FlatData, T> selector = datum -> (T) datum.get(0);
                ColumnMapper<FlatData> newMapper = mapper.compose(selector);
                result.add(newMapper);
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void collectDeepMapper(ColumnMapper<?> fieldList, int deepLevel, DataTemplate<FlatData> result) {
        if (fieldList.isListField()) {
            for (ColumnMapper<?> mapper : fieldList.fieldTemplate) {
                if (mapper.isListField()) {
                    collectDeepMapper(mapper, deepLevel + 1, result);
                } else {
                    Function selector = (Function<FlatData, ?>) datum -> datum.get(deepLevel);
                    ColumnMapper<FlatData> newMapper = mapper.<FlatData>compose(selector);
                    result.add(newMapper);
                }
            }
        }
    }

    Collection<FlatData> flattenData(Collection<?> data) {
        Collection<FlatData> result = new ArrayList<>();

        ColumnMapper<?> deepField = this.getDeepField();

        for (Object datum : data) {
            FlatData seed = new FlatData();
            seed.add(datum);
            result.addAll(flattenData(seed, deepField));
        }

        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Collection<FlatData> flattenData(FlatData seed, ColumnMapper<?> field) {
        ArrayList<FlatData> result = new ArrayList<>();

        Object datum = seed.getLast();
        Function mapper = field.getMapper();
        Object collection = mapper.apply(datum);

        if (collection instanceof Collection) {
            ColumnMapper<?> deepField = field.fieldTemplate.getDeepField();

            for (Object element : ((Collection) collection)) {
                FlatData item = seed.makeCopy();
                item.add(element);
                if (deepField != null) {
                    result.addAll(flattenData(item, deepField));
                } else {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Configuration builder for {@link DataTemplate}.
     *
     * @param <T> DTO of {@link DataTemplate}
     */
    public static class Builder<T> {
        private final DataTemplate<T> template;

        public Builder(DataTemplate<T> template) {
            this.template = template;
        }

        /**
         * Specify the beginning cell to write data (includes title row).
         *
         * @param rowAt row index (from 0)
         * @param colAt column index (from 0)
         * @return current builder
         */
        @SneakyThrows
        public Builder<T> startAtCell(int rowAt, int colAt) {
            if (rowAt < 0 || colAt < 0) {
                throw new RuntimeException("Cell coordinate must be from (0, 0)");
            }
            template.rowAt = rowAt;
            template.colAt = colAt;
            return this;
        }

        /**
         * Specify the beginning cell to write data (includes title row).
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
                throw new RuntimeException("Error while parsing cell address", e);
            }
        }

        /**
         * Auto resizing width of all columns.
         *
         * @param b boolean
         * @return current builder
         */
        public Builder<T> autoSizeColumns(boolean b) {
            template.autoSizeColumns = b;
            return this;
        }

        /**
         * Prevent the title row from being written.
         *
         * @param b boolean
         * @return current builder
         */
        public Builder<T> noHeader(boolean b) {
            template.noHeader = b;
            return this;
        }

        /**
         * Set custom style for title row.
         *
         * @param style {@link Style}
         * @return current builder
         */
        public Builder<T> headerStyle(Style style) {
            template.headerStyle = style;
            return this;
        }

        /**
         * Set custom style for data section (all data rows).
         *
         * @param style {@link Style}
         * @return current builder
         */
        public Builder<T> dataStyle(Style style) {
            template.dataStyle = style;
            return this;
        }

        /**
         * Set style for a specific data row.
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
