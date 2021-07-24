package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyDescriptor;
import java.util.function.Function;

/**
 * Mapper that contains rule to map property of DTO into column value
 *
 * @param <T> DTO type
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class ColumnMapper<T> {

    private String fieldName;
    private String displayName;
    private Function<T, ?> mapper;

    private boolean mergeOnValue;
    private Function<T, ?> mergeOnId;

    private Style style;
    @Getter(AccessLevel.NONE)
    private Function<T, Style> conditionalStyle;

    // Column setting
    private Integer pxWidth;
    private boolean autoSize;

    ColumnMapper() {
    }

    public String getField() {
        return fieldName;
    }

    public String getTitle() {
        return displayName;
    }

    /**
     * Map DTO based on field name
     *
     * @param fieldName field name of DTO
     * @return current mapper
     */
    public ColumnMapper<T> field(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    /**
     * Set title for the column
     *
     * @param displayName column title
     * @return current mapper
     */
    public ColumnMapper<T> title(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Map DTO based on a custom function
     *
     * @param mapper function to extract value from DTO
     * @return current mapper
     */
    public ColumnMapper<T> transform(Function<T, ?> mapper) {
        this.mapper = mapper;
        return this;
    }

    /**
     * Merge consecutively cells on current column by comparing cell value
     *
     * @param b
     * @return current mapper
     */
    public ColumnMapper<T> mergeOnValue(boolean b) {
        this.mergeOnValue = b;
        return this;
    }

    /**
     * Merge consecutively cells on current column by comparing a specific value of DTO
     *
     * @param idExtractor function to specify compared value of DTO
     * @return current mapper
     */
    public ColumnMapper<T> mergeOnId(Function<T, ?> idExtractor) {
        this.mergeOnId = idExtractor;
        return this;
    }

    /**
     * Set style for current column (accumulates to data style if specified)
     *
     * @param style {@link Style}
     * @return current mapper
     */
    public ColumnMapper<T> style(Style style) {
        this.style = style;
        return this;
    }

    /**
     * Set style for cell based on specific condition of DTO
     *
     * @param style a function that return a {@link Style}
     * @return current mapper
     */
    public ColumnMapper<T> conditionalStyle(Function<T, Style> style) {
        this.conditionalStyle = style;
        return this;
    }

    /**
     * Set width to the current column
     *
     * @param pixels width in pixel value
     * @return current mapper
     */
    public ColumnMapper<T> width(Integer pixels) {
        this.pxWidth = pixels;
        return this;
    }

    /**
     * Auto resizing current column
     *
     * @param b boolean
     * @return current mapper
     */
    public ColumnMapper<T> autoSize(boolean b) {
        this.autoSize = b;
        return this;
    }

    Object retrieveValue(T object, Class<T> tClass) {
        if (mapper == null) {
            // use entity's getter
            PropertyDescriptor field = ReflectUtil.getField(fieldName, tClass);
            if (field == null) {
                return null;
            }
            try {
                return field.getReadMethod().invoke(object);
            } catch (Exception e) {
                return null;
            }

        } else {
            // use mapper to get value
            try {
                return mapper.apply(object);
            } catch (Exception e) {
                return null;
            }
        }
    }

    Style applyConditionalStyle(T object) {
        return ReflectUtil.safeApply(conditionalStyle, object);
    }

    Object retrievePivotValueForMergeComparison(T object, Object cellValue) {
        Object id = ReflectUtil.safeApply(mergeOnId, object);
        if (id != null) {
            return id;
        }
        return cellValue;
    }

    boolean needMerged() {
        return mergeOnValue || mergeOnId != null;
    }
}