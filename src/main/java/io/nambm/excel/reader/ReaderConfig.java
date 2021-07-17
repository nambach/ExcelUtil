package io.nambm.excel.reader;

import io.nambm.excel.model.func.ConsumerBoolean;
import io.nambm.excel.model.func.ConsumerDate;
import io.nambm.excel.model.func.ConsumerDouble;
import io.nambm.excel.model.func.ConsumerString;
import io.nambm.excel.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class ReaderConfig<T> {

    private Class<T> tClass;

    private Set<Integer> sheetIndexes = new TreeSet<>();
    private int dataFromRow = 1;

    private int metadataRowIndex = -1;
    private int titleRowIndex = -1;

    private Map<Integer, String> fieldNameByColIndex = new HashMap<>();
    private Map<Integer, BiConsumer<T, ?>> setterByColIndex = new HashMap<>();
    private Map<String, BiConsumer<T, ?>> setterByColMetadata = new HashMap<>();
    private Map<String, BiConsumer<T, ?>> setterByColTitle = new HashMap<>();

    ReaderConfig(Class<T> tClass) {
        this.tClass = tClass;
    }

    public static <T> ReaderConfig<T> fromClass(Class<T> tClass) {
        return new ReaderConfig<>(tClass);
    }

    BiConsumer<T, ?> getConsumer(int index, String fieldName, String columnTitle) {
        if (setterByColIndex.containsKey(index)) {
            return setterByColIndex.get(index);
        }
        if (setterByColMetadata.containsKey(fieldName)) {
            return setterByColMetadata.get(fieldName);
        }
        if (setterByColTitle.containsKey(columnTitle)) {
            return setterByColTitle.get(columnTitle);
        }
        return null;
    }

    public ReaderConfig<T> sheets(int... indexes) {
        Objects.requireNonNull(indexes);
        for (int index : indexes) {
            if (index >= 0) {
                sheetIndexes.add(index);
            }
        }
        return this;
    }

    public ReaderConfig<T> rowMetadataAt(int index) {
        this.metadataRowIndex = index;
        return this;
    }

    public ReaderConfig<T> rowTitleAt(int index) {
        this.titleRowIndex = index;
        return this;
    }

    public ReaderConfig<T> rowDataFrom(int index) {
        this.dataFromRow = index;
        return this;
    }

    public ReaderConfig<T> column(int index, String fieldName) {
        if (index >= 0 && ReflectUtil.getField(fieldName, tClass) != null) {
            this.fieldNameByColIndex.put(index, fieldName);
        }
        return this;
    }

    public ReaderConfig<T> handlers(Function<HandlerBuilder<T>, HandlerBuilder<T>> func) {
        Objects.requireNonNull(func);
        HandlerBuilder<T> handlerBuilder = new HandlerBuilder<>(this);
        func.apply(handlerBuilder);
        return this;
    }

    public static class HandlerBuilder<T> {
        private final ReaderConfig<T> config;

        public HandlerBuilder(ReaderConfig<T> config) {
            this.config = config;
        }

        // by index
        public HandlerBuilder<T> byColIndex(int columnIndex, ConsumerString<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColIndex.put(columnIndex, consumer);
            return this;
        }

        public HandlerBuilder<T> byColIndex(int columnIndex, ConsumerDouble<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColIndex.put(columnIndex, consumer);
            return this;
        }

        public HandlerBuilder<T> byColIndex(int columnIndex, ConsumerBoolean<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColIndex.put(columnIndex, consumer);
            return this;
        }

        public HandlerBuilder<T> byColIndex(int columnIndex, ConsumerDate<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColIndex.put(columnIndex, consumer);
            return this;
        }

        // by fieldName
        public HandlerBuilder<T> byMetadata(String fieldName, ConsumerString<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColMetadata.put(fieldName, consumer);
            return this;
        }

        public HandlerBuilder<T> byMetadata(String fieldName, ConsumerDouble<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColMetadata.put(fieldName, consumer);
            return this;
        }

        public HandlerBuilder<T> byMetadata(String fieldName, ConsumerBoolean<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColMetadata.put(fieldName, consumer);
            return this;
        }

        public HandlerBuilder<T> byMetadata(String fieldName, ConsumerDate<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColMetadata.put(fieldName, consumer);
            return this;
        }

        // by title
        public HandlerBuilder<T> byTitle(String columnTitle, ConsumerString<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColTitle.put(columnTitle, consumer);
            return this;
        }

        public HandlerBuilder<T> byTitle(String columnTitle, ConsumerDouble<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColTitle.put(columnTitle, consumer);
            return this;
        }

        public HandlerBuilder<T> byTitle(String columnTitle, ConsumerBoolean<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColTitle.put(columnTitle, consumer);
            return this;
        }

        public HandlerBuilder<T> byTitle(String columnTitle, ConsumerDate<T> consumer) {
            Objects.requireNonNull(consumer);
            config.setterByColTitle.put(columnTitle, consumer);
            return this;
        }
    }
}
