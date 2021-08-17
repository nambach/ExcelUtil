package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.CacheStyle;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.PixelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static io.github.nambach.excelutil.util.ListUtil.groupBy;

class BaseWriter implements BaseEditor {

    static final Map<Class<?>, BiConsumer<Cell, Object>> writerHandler = new HashMap<Class<?>, BiConsumer<Cell, Object>>() {{
        put(String.class, (cell, val) -> cell.setCellValue((String) val));
        put(Long.class, (cell, val) -> cell.setCellValue((long) val));
        put(Integer.class, (cell, val) -> cell.setCellValue((int) val));
        put(Double.class, (cell, val) -> cell.setCellValue((double) val));
        put(Float.class, (cell, val) -> cell.setCellValue((float) val));
        put(Boolean.class, (cell, val) -> cell.setCellValue((boolean) val));
        put(Date.class, (cell, val) -> cell.setCellValue((Date) val));
    }};

    static final Style DATE = Style.builder().datePattern("MMM dd, yyyy").build();
    final CacheStyle cachedStyles;

    BaseWriter(Workbook workbook, Editor.Mode mode) {
        cachedStyles = new CacheStyle(workbook, mode);
    }

    public <T> void writeData(Sheet sheet, DataTemplate<T> template, Collection<T> data, int rowAt, int colAt) {
        if (template.hasDeepLevel()) {
            DataTemplate<FlatData> flatTemplate = template.getFlatTemplate();
            Collection<FlatData> flatData = template.flattenData(data);
            internalWriteData(sheet, flatTemplate, flatData, rowAt, colAt);
        } else {
            internalWriteData(sheet, template, data, rowAt, colAt);
        }
    }

    private <T> void internalWriteData(Sheet sheet, DataTemplate<T> template, Collection<T> data, int rowAt, int colAt) {
        if (data == null) {
            data = Collections.emptyList();
        }
        if (rowAt < 0) {
            rowAt = 0;
        }
        if (colAt < 0) {
            colAt = 0;
        }

        Style headerStyle = template.getHeaderStyle();
        Style dataStyle = template.getDataStyle();

        // This is title row
        if (!template.isNoHeader()) {
            Row headerRow = getRowAt(sheet, rowAt++);
            CellStyle defaultHeaderStyle = cachedStyles.accumulate(headerStyle);
            int cellCount = colAt;
            for (ColumnMapper<T> mapper : template) {
                Cell cell = getCellAt(headerRow, cellCount++);
                cell.setCellValue(mapper.getDisplayName());
                cell.setCellStyle(defaultHeaderStyle);
            }
        }

        // Merge tracker
        Map<Integer, MergeItem> mergeTracker = new HashMap<>();

        // Create data rows
        int objectCount = 0;
        for (T object : data) {
            objectCount++;

            Row dataRow = getRowAt(sheet, rowAt++);

            // Create all columns in a row
            int i = colAt - 1;
            for (ColumnMapper<T> mapper : template) {
                i++;

                Cell cell = getCellAt(dataRow, i);
                Object cellValue = mapper.retrieveValue(object);
                if (cellValue == null) {
                    cell.setCellValue("");
                } else {
                    BiConsumer<Cell, Object> handler = writerHandler.get(cellValue.getClass());
                    if (handler != null) {
                        handler.accept(cell, cellValue);
                    } else {
                        cell.setCellValue(cellValue.toString());
                    }
                }

                // set cell style
                Style dateStyle = isDateType(cellValue) ? DATE : null;
                Style conditionalStyle = mapper.applyConditionalStyle(object);
                Style columnStyle = mapper.getStyle();
                Style rowStyle = template.applyConditionalRowStyle(object);
                CellStyle cellStyle = cachedStyles.accumulate(dateStyle, dataStyle, rowStyle, columnStyle, conditionalStyle);
                cell.setCellStyle(cellStyle);

                // do merge
                if (mapper.needMerged()) {
                    int rowNum = dataRow.getRowNum();
                    Object currentValue = mapper.retrievePivotValueForMergeComparison(object, cellValue);

                    if (objectCount == 1) {
                        // First row => init tracker item
                        mergeTracker.putIfAbsent(i, new MergeItem(currentValue, rowNum, rowNum));
                    } else {
                        MergeItem tracker = mergeTracker.get(i);

                        if (Objects.equals(currentValue, tracker.getLastValue())) {
                            // Same value found => increase merge range
                            tracker.increaseRange();
                            if (objectCount == data.size()) {
                                tracker.handleMerge(cell);
                            }
                        } else {
                            // New value coped => finish last merge range
                            tracker.handleMerge(cell);
                            tracker.reset(currentValue, rowNum);
                        }
                    }
                }

                // Resize columns when last row reached
                if (objectCount == data.size()) {
                    Integer pxWidth = mapper.getPxWidth();
                    if (pxWidth != null && pxWidth > 0) {
                        PixelUtil.setColumnWidth(sheet, cell.getColumnIndex(), pxWidth);
                    } else if (template.isAutoSizeColumns() || mapper.isAutoSize()) {
                        sheet.autoSizeColumn(i, mapper.needMerged());
                    }
                }

            } // end of (mapper : mappers)
        } // end of (T object : data)
    }

    public void writeTemplate(Sheet sheet, Template template) {
        Map<Integer, List<WriterCell>> lines = groupBy(template, WriterCell::getRowAt);
        lines.forEach((line, cells) -> {
            Row row = getRowAt(sheet, line);
            for (WriterCell writerCell : cells) {
                Cell cell = getCellAt(row, writerCell.getColAt());
                writeCellInfo(writerCell, cell);
            }
        });
    }

    public void writeCellInfo(WriterCell writerCell, Cell cell) {

        // Set core value
        boolean isDate = false;
        if (writerCell.getContent() != null) {
            cell.setCellValue(writerCell.getContent());
        } else if (writerCell.getValue() != null) {
            cell.setCellValue(writerCell.getValue());
        } else if (writerCell.getDate() != null) {
            cell.setCellValue(writerCell.getDate());
            isDate = true;
        }

        // Set styles
        CellStyle cellStyle = cachedStyles.accumulate(writerCell.getStyle(), (isDate ? DATE : null));
        cell.setCellStyle(cellStyle);

        int rowSpan = writerCell.getRowSpan();
        int colSpan = writerCell.getColSpan();
        if (colSpan > 1 || rowSpan > 1) {
            int rowAt = writerCell.getRowAt();
            int colAt = writerCell.getColAt();

            cell.getSheet()
                .addMergedRegion(new CellRangeAddress(rowAt, rowAt + rowSpan - 1,
                                                      colAt, colAt + colSpan - 1));
        }
    }

}
