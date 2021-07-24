package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    // Store value as list to stack up multiple handlers on a same column
    private Map<Integer, Handlers<T>> handlerMap = new HashMap<>();

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

    /**
     * Map the cell value at a column into target field of DTO.
     *
     * @param index     index of the column (from 0)
     * @param fieldName field name of DTO to map
     * @return current config
     */
    public ReaderConfig<T> column(int index, String fieldName) {
        if (index >= 0 && ReflectUtil.getField(fieldName, tClass) != null) {
            Handler<T> handler = new Handler<>();
            handler.atColumn(index).field(fieldName);

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
            throw new Exception("Handler must be provided a column index with atColumn() or fromColumn()");
        }
        if (handler.getIndex() != null) {
            int index = handler.getIndex();
            Handlers<T> list = handlerMap.getOrDefault(index, new Handlers<>());
            list.add(handler);
            handlerMap.put(index, list);
        }

        return this;
    }

    /**
     * Read data from Excel and convert to list of {@link Raw} data.
     *
     * @param stream     byte stream
     * @param sheetIndex index of sheet to read
     * @return list of {@link Raw} DTO
     */
    public List<Raw<T>> readSheetRaw(InputStream stream, int sheetIndex) {
        Pointer base = getBaseCoordinate();
        Editor editor = new Editor(stream);
        return editor
                .goToSheet(sheetIndex)
                .goToCell(base.getRow(), base.getCol())
                .readSectionRaw(this);
    }

    /**
     * Read data from Excel and convert to list of data.
     *
     * @param stream byte stream
     * @return list of DTO
     */
    public List<T> readSheet(InputStream stream) {
        return convert(readSheetRaw(stream, 0));
    }

    /**
     * Read data from Excel and convert to list of data.
     *
     * @param stream     byte stream
     * @param sheetIndex index of sheet to read
     * @return list of DTO
     */
    public List<T> readSheet(InputStream stream, int sheetIndex) {
        return convert(readSheetRaw(stream, sheetIndex));
    }

    /**
     * Read data from multiple sheets and convert to a map of list of {@link Raw} data.
     *
     * @param stream       byte stream
     * @param sheetIndexes indexes of sheet to read
     * @return map of list of {@link Raw} DTO, having key map is the sheet name
     */
    public Map<String, List<Raw<T>>> readSheetsRaw(InputStream stream, int... sheetIndexes) {
        Objects.requireNonNull(sheetIndexes);
        Set<Integer> allowed = new HashSet<>();
        for (int sheetIndex : sheetIndexes) {
            allowed.add(sheetIndex);
        }

        Pointer base = getBaseCoordinate();
        Editor editor = new Editor(stream);
        Map<String, List<Raw<T>>> result = new LinkedHashMap<>();
        for (int i = 0; i < editor.getTotalSheets(); i++) {
            if (!allowed.contains(i)) {
                continue;
            }
            List<Raw<T>> rawList = editor
                    .goToSheet(i)
                    .goToCell(base.getRow(), base.getCol())
                    .readSectionRaw(this);
            result.put(editor.getSheetName(), rawList);
        }
        return result;
    }

    /**
     * Read data from multiple sheets and convert to a map of list of data.
     *
     * @param stream       byte stream
     * @param sheetIndexes indexes of sheet to read
     * @return map of list of DTO, having key map is the sheet name
     */
    public Map<String, List<T>> readSheets(InputStream stream, int... sheetIndexes) {
        Map<String, List<Raw<T>>> result = readSheetsRaw(stream, sheetIndexes);
        return result.entrySet().stream()
                     .collect(Collectors.toMap(Map.Entry::getKey, e -> convert(e.getValue())));
    }

    /**
     * Read data from all sheets and convert to a map of list of {@link Raw} data.
     *
     * @param stream byte stream
     * @return map of list of {@link Raw} DTO, having key map is the sheet name
     */
    public Map<String, List<Raw<T>>> readAllSheetsRaw(InputStream stream) {
        Pointer base = getBaseCoordinate();
        Editor editor = new Editor(stream);
        Map<String, List<Raw<T>>> result = new LinkedHashMap<>();
        for (int i = 0; i < editor.getTotalSheets(); i++) {
            List<Raw<T>> rawList = editor
                    .goToSheet(i)
                    .goToCell(base.getRow(), base.getCol())
                    .readSectionRaw(this);
            result.put(editor.getSheetName(), rawList);
        }
        return result;
    }

    /**
     * Read data from all sheets and convert to a map of list of data.
     *
     * @param stream byte stream
     * @return map of list of DTO, having key map is the sheet name
     */
    public Map<String, List<T>> readAllSheets(InputStream stream) {
        Map<String, List<Raw<T>>> result = readAllSheetsRaw(stream);
        return result.entrySet().stream()
                     .collect(Collectors.toMap(Map.Entry::getKey, e -> convert(e.getValue())));
    }

    private List<T> convert(List<Raw<T>> rawList) {
        return rawList.stream().map(Raw::getData).collect(Collectors.toList());
    }
}
