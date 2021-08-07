package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ReflectUtil;
import io.github.nambach.excelutil.validator.builtin.TypeValidator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A configuration object containing rules for reading
 * data from Excel table and map to DTO class.
 *
 * @param <T>
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class ReaderConfig<T> {

    private Class<T> tClass;
    private int dataFromIndex = 1;
    private int titleRowIndex = 0;
    private boolean earlyExist;

    // Store value as list to stack up multiple handlers on a same column
    private Map<Integer, Handlers<T>> handlerMap = new HashMap<>();

    private BiConsumer<T, ReaderRow> beforeAddItemHandle;

    ReaderConfig(Class<T> tClass) {
        this.tClass = tClass;
    }

    /**
     * Specify the DTO type.
     *
     * @param tClass DTO type
     * @param <T>    DTO
     * @return current config
     */
    public static <T> ReaderConfig<T> fromClass(Class<T> tClass) {
        return new ReaderConfig<>(tClass);
    }

    private Pointer getBaseCoordinate() {
        int baseRow = titleRowIndex >= 0 ? titleRowIndex : dataFromIndex;
        int baseCol = handlerMap.keySet().stream().reduce(Math::min).orElse(0);
        return new Pointer(baseRow, baseCol);
    }

    //   if: dest - src = offset
    // then: dest = offset + src
    ReaderConfig<T> translate(int rowAt, int colAt) {
        Pointer base = getBaseCoordinate();
        int baseRow = base.getRow();
        int baseCol = base.getCol();

        int rowOffset = rowAt - baseRow;
        int colOffset = colAt - baseCol;

        if (rowOffset == 0 && colOffset == 0) {
            return this;
        }

        ReaderConfig<T> copy = new ReaderConfig<>(tClass);
        if (titleRowIndex >= 0) {
            copy.titleRowIndex = titleRowIndex + rowOffset;
        }
        copy.dataFromIndex = dataFromIndex + rowOffset;
        handlerMap.forEach((i, handler) -> copy.handlerMap.put(i + colOffset, handler));

        // other data
        copy.earlyExist = earlyExist;
        copy.beforeAddItemHandle = beforeAddItemHandle;
        return copy;
    }

    /**
     * Specify where the title row is at (default is 0).
     * Set this value to -1 if there is no title row.
     *
     * @param index title row index
     * @return current config
     */
    public ReaderConfig<T> titleAtRow(int index) {
        this.titleRowIndex = index;
        return this;
    }

    /**
     * Specify where the data row starts from (default is 1).
     *
     * @param index first data row index
     * @return current config
     */
    public ReaderConfig<T> dataFromRow(int index) {
        this.dataFromIndex = index;
        return this;
    }

    public ReaderConfig<T> existWhenValidationFailed(boolean b) {
        this.earlyExist = b;
        return this;
    }

    /**
     * Map the cell value at a column into target field of DTO.
     *
     * @param index     index of the column (from 0)
     * @param fieldName field name of DTO to map
     * @return current config
     */
    public ReaderConfig<T> column(int index, String fieldName) {
        return column(index, fieldName, null);
    }

    public ReaderConfig<T> column(int index, String fieldName, TypeValidator typeValidator) {
        if (index >= 0 && ReflectUtil.getField(fieldName, tClass) != null) {
            Handler<T> handler = new Handler<T>()
                    .atColumn(index)
                    .field(fieldName)
                    .validate(typeValidator);

            Handlers<T> list = handlerMap.getOrDefault(index, new Handlers<>());
            list.add(handler);
            handlerMap.put(index, list);
        }
        return this;
    }

    /**
     * Specify a handler function to process data cell at a column.
     *
     * @param func a function that builds {@link Handler}
     * @return current config
     */
    @SneakyThrows
    public ReaderConfig<T> handler(Function<Handler<T>, Handler<T>> func) {
        Objects.requireNonNull(func);

        Handler<T> handler = new Handler<>();
        func.apply(handler);
        if (handler.getColAt() == null && handler.getColFrom() == null) {
            throw new Exception("Handler must be provided a column index with .atColumn() or .fromColumn()");
        }
        if (handler.getIndex() != null) {
            int index = handler.getIndex();
            Handlers<T> list = handlerMap.getOrDefault(index, new Handlers<>());
            list.add(handler);
            handlerMap.put(index, list);
        }

        return this;
    }

    public ReaderConfig<T> beforeAddingItem(BiConsumer<T, ReaderRow> handler) {
        this.beforeAddItemHandle = ReflectUtil.safeWrap(handler);
        return this;
    }

    boolean handleBeforeAdd() {
        return this.beforeAddItemHandle != null;
    }

    /**
     * Read data from Excel and convert to list of data.
     *
     * @param stream     byte stream
     * @param sheetIndex index of sheet to read
     * @return list of DTO
     */
    public Result<T> readSheet(InputStream stream, int sheetIndex) {
        Pointer base = getBaseCoordinate();
        try (Editor editor = new Editor(stream)) {
            return editor
                    .goToSheet(sheetIndex)
                    .goToCell(base.getRow(), base.getCol())
                    .readSection(this);
        }
    }

    /**
     * Read data from Excel and convert to list of data.
     *
     * @param stream byte stream
     * @return list of DTO
     */
    public Result<T> readSheet(InputStream stream) {
        return readSheet(stream, 0);
    }

    /**
     * Read data from Excel and convert to list of data.
     *
     * @param stream    byte stream
     * @param sheetName name of sheet to read
     * @return list of DTO
     */
    public List<T> readSheet(InputStream stream, String sheetName) {
        Pointer base = getBaseCoordinate();
        try (Editor editor = new Editor(stream)) {
            int index = editor.getPoiWorkbook().getSheetIndex(sheetName);
            return editor
                    .goToSheet(index)
                    .goToCell(base.getRow(), base.getCol())
                    .readSection(this);
        }
    }

    /**
     * Read data from multiple sheets and convert to a map of list of data.
     *
     * @param stream       byte stream
     * @param sheetIndexes indexes of sheet to read
     * @return map of list of DTO, having key map is the sheet name
     */
    public Map<String, Result<T>> readSheets(InputStream stream, int... sheetIndexes) {
        Objects.requireNonNull(sheetIndexes);

        Set<Integer> indexes = new HashSet<>();
        for (int sheetIndex : sheetIndexes) {
            indexes.add(sheetIndex);
        }

        Pointer base = getBaseCoordinate();
        try (Editor editor = new Editor(stream)) {
            Map<String, Result<T>> result = new LinkedHashMap<>();
            for (int i = 0; i < editor.getTotalSheets(); i++) {
                if (!indexes.contains(i)) {
                    continue;
                }
                Result<T> list = editor
                        .goToSheet(i)
                        .goToCell(base.getRow(), base.getCol())
                        .readSection(this);
                result.put(editor.getSheetName(), list);
            }
            return result;
        }
    }

    /**
     * Read data from multiple sheets and convert to a map of list of data.
     *
     * @param stream     byte stream
     * @param sheetNames indexes of sheet to read
     * @return map of list of DTO, having key map is the sheet name
     */
    public Map<String, Result<T>> readSheets(InputStream stream, String... sheetNames) {
        Objects.requireNonNull(sheetNames);
        Set<String> names = new HashSet<>(Arrays.asList(sheetNames));

        Pointer base = getBaseCoordinate();
        try (Editor editor = new Editor(stream)) {
            Map<String, Result<T>> result = new LinkedHashMap<>();
            for (String name : names) {
                int i = editor.getPoiWorkbook().getSheetIndex(name);
                if (i < 0) {
                    continue;
                }
                Result<T> list = editor
                        .goToSheet(i)
                        .goToCell(base.getRow(), base.getCol())
                        .readSection(this);
                result.put(editor.getSheetName(), list);
            }
            return result;
        }
    }

    /**
     * Read data from all sheets and convert to a map of list of data.
     *
     * @param stream byte stream
     * @return map of list of DTO, having key map is the sheet name
     */
    public Map<String, Result<T>> readAllSheets(InputStream stream) {
        Pointer base = getBaseCoordinate();
        try (Editor editor = new Editor(stream)) {
            Map<String, Result<T>> result = new LinkedHashMap<>();
            for (int i = 0; i < editor.getTotalSheets(); i++) {
                Result<T> list = editor
                        .goToSheet(i)
                        .goToCell(base.getRow(), base.getCol())
                        .readSection(this);
                result.put(editor.getSheetName(), list);
            }
            return result;
        }
    }
}
