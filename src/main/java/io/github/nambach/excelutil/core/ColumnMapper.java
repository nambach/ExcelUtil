package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyDescriptor;
import java.util.function.Function;

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

    ColumnMapper() {
    }

    public String getField() {
        return fieldName;
    }

    public String getTitle() {
        return displayName;
    }

    public ColumnMapper<T> field(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public ColumnMapper<T> title(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ColumnMapper<T> transform(Function<T, ?> mapper) {
        this.mapper = mapper;
        return this;
    }

    public ColumnMapper<T> mergeOnValue(boolean b) {
        this.mergeOnValue = b;
        return this;
    }

    public ColumnMapper<T> mergeOnId(Function<T, ?> idExtractor) {
        this.mergeOnId = idExtractor;
        return this;
    }

    public ColumnMapper<T> style(Style style) {
        this.style = style;
        return this;
    }

    public ColumnMapper<T> conditionalStyle(Function<T, Style> style) {
        this.conditionalStyle = style;
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