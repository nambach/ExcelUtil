package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.CacheStyle;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.PixelUtil;
import io.github.nambach.excelutil.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
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

class BaseWriter extends BaseEditor {

    static final Style DATE = Style.builder().date(true).build();
    final CacheStyle cachedStyles;

    BaseWriter(Workbook workbook) {
        cachedStyles = new CacheStyle(workbook);
    }

    public <T> void writeData(Sheet sheet, DataTemplate<T> template, Collection<T> data, int rowAt, int colAt) {
        if (data == null) {
            data = Collections.emptyList();
        }
        if (rowAt < 0) {
            rowAt = 0;
        }
        if (colAt < 0) {
            colAt = 0;
        }

        List<ColumnMapper<T>> mappers = template.getMappers();
        Style headerStyle = template.getHeaderStyle();
        Style dataStyle = template.getDataStyle();

        // This row to store entity's metadata
//        if (template.isReuseForImport()) {
//            Row metadataRow = getRowAt(sheet, rowAt++);
//            int cellCount = colAt;
//            for (ColumnMapper<T> mapper : mappers) {
//                Cell cell = metadataRow.createCell(cellCount++, CellType.STRING);
//                cell.setCellValue(mapper.getFieldName());
//            }
//            // Hide metadata row
//            metadataRow.setZeroHeight(true);
//        }

        // This is title row
        if (!template.isNoHeader()) {
            Row headerRow = getRowAt(sheet, rowAt++);
            CellStyle defaultHeaderStyle = cachedStyles.accumulate(headerStyle);
            int cellCount = colAt;
            for (ColumnMapper<T> mapper : mappers) {
                Cell cell = headerRow.createCell(cellCount++, CellType.STRING);
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
            for (ColumnMapper<T> mapper : mappers) {
                i++;

                Object cellValue = mapper.retrieveValue(object, template.getTClass());

                Cell cell = null;
                ReflectUtil.Type type = ReflectUtil.determineType(cellValue);
                switch (type) {
                    case STRING:
                        cell = dataRow.createCell(i, CellType.STRING);
                        if (cellValue != null) {
                            cell.setCellValue((String) cellValue);
                        } else {
                            cell.setCellValue("");
                        }
                        break;
                    case LONG:
                        cell = dataRow.createCell(i, CellType.NUMERIC);
                        cell.setCellValue((Long) cellValue);
                        break;
                    case INTEGER:
                        cell = dataRow.createCell(i, CellType.NUMERIC);
                        cell.setCellValue((Integer) cellValue);
                        break;
                    case DOUBLE:
                        cell = dataRow.createCell(i, CellType.NUMERIC);
                        cell.setCellValue((Double) cellValue);
                        break;
                    case FLOAT:
                        cell = dataRow.createCell(i, CellType.NUMERIC);
                        cell.setCellValue((Float) cellValue);
                        break;
                    case BOOLEAN:
                        cell = dataRow.createCell(i, CellType.BOOLEAN);
                        cell.setCellValue((Boolean) cellValue);
                        break;
                    case DATE:
                        cell = dataRow.createCell(i);
                        cell.setCellValue((Date) cellValue);
                        if (mapper.getStyle() == null) {
                            mapper.setStyle(DATE);
                        } else if (!mapper.getStyle().isDate()) {
                            // only allow direct accumulate style to .setStyle()
                            Style dateStyle = cachedStyles.unsafeAccumulate(mapper.getStyle(), DATE);
                            mapper.setStyle(dateStyle);
                        }
                        break;
                    case OBJECT:
                        cell = dataRow.createCell(i, CellType.STRING);
                        if (cellValue != null) {
                            cell.setCellValue(cellValue.toString());
                        } else {
                            cell.setCellValue("");
                        }
                        break;
                }

                // set cell style
                Style conditionalStyle = mapper.applyConditionalStyle(object);
                Style columnStyle = mapper.getStyle();
                Style rowStyle = template.applyConditionalRowStyle(object);
                CellStyle cellStyle = cachedStyles.accumulate(dataStyle, rowStyle, columnStyle, conditionalStyle);
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
                                tracker.handleMerge(sheet, cell);
                            }
                        } else {
                            // New value coped => finish last merge range
                            tracker.handleMerge(sheet, cell);
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
        for (WriterCell writerCell : template) {
            this.writeCellInfo(sheet, writerCell);
        }
    }

    public void writeCellInfo(Sheet sheet, WriterCell writerCell) {
        Row row = getRowAt(sheet, writerCell.getRowAt());
        Cell cell = getCellAt(row, writerCell.getColAt());

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

        int rowAt = writerCell.getRowAt();
        int colAt = writerCell.getColAt();
        int rowSpan = writerCell.getRowSpan();
        int colSpan = writerCell.getColSpan();
        if (colSpan > 1 || rowSpan > 1) {

            // Spread style to whole range
            for (int colOffset = 0; colOffset < colSpan; colOffset++) {
                for (int rowOffset = 0; rowOffset < rowSpan; rowOffset++) {
                    if (rowOffset == 0 && colOffset == 0) {
                        continue;
                    }
                    Row currentRow = getRowAt(sheet, rowAt + rowOffset);
                    Cell currentCell = getCellAt(currentRow, colAt + colOffset);
                    currentCell.setCellStyle(cellStyle);
                }
            }

            sheet.addMergedRegion(new CellRangeAddress(rowAt, rowAt + rowSpan - 1,
                                                       colAt, colAt + colSpan - 1));
        }
    }

}
