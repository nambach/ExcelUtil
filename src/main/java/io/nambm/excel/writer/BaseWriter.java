package io.nambm.excel.writer;

import io.nambm.excel.style.CacheStyle;
import io.nambm.excel.style.DefaultStyle;
import io.nambm.excel.style.Style;
import io.nambm.excel.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class BaseWriter {

    final CacheStyle cachedStyles;
    final Workbook workbook;
    final List<Sheet> sheets;
    boolean firstRowReached;

    Style headerStyle;
    Style dataStyle;

    private BaseWriter() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        this.workbook = workbook;
        this.cachedStyles = new CacheStyle(workbook);
        this.sheets = new ArrayList<>();
    }

    public BaseWriter(Table<?> configuration) {
        this();

        if (configuration != null) {
            this.headerStyle = configuration.getHeaderStyle();
            this.dataStyle = configuration.getDataStyle();
        }
    }

    int getLastRowIndex(Sheet sheet) {
        int rowCount = Math.max(sheet.getLastRowNum(), 0);
        if (!firstRowReached) {
            firstRowReached = true;
            return rowCount;
        }
        return rowCount + 1;
    }

    Sheet getLastSheet() {
        if (sheets.isEmpty()) {
            return createNewSheet("Sheet 1");
        }
        return sheets.get(sheets.size() - 1);
    }

    Row getRowAt(Sheet sheet, int rowAt) {
        Row row = sheet.getRow(rowAt);
        if (row == null) {
            row = sheet.createRow(rowAt);
        }
        return row;
    }

    Cell getCellAt(Row row, int colAt) {
        Cell cell = row.getCell(colAt);
        if (cell == null) {
            cell = row.createCell(colAt, CellType.STRING);
        }
        return cell;
    }

    public Sheet createNewSheet(String sheetName) {
        firstRowReached = false;
        Sheet sheet = workbook.createSheet(sheetName);
        sheets.add(sheet);
        return sheet;
    }

    public <T> void writeDataIntoSheet(Sheet sheet, Collection<T> data, Table<T> table, int rowAt, int colAt) {

        if (data == null) {
            data = Collections.emptyList();
        }
        if (rowAt < 0) {
            rowAt = 0;
        }
        if (colAt < 0) {
            colAt = 0;
        }

        List<ColumnMapper<T>> mappers = table.getMappers();
        Style internalHeaderStyle = table.getHeaderStyle();
        Style internalDataStyle = table.getDataStyle();

        // This row to store entity's metadata
        if (table.isReuseForImport()) {
            Row metadataRow = getRowAt(sheet, rowAt++);
            int cellCount = colAt;
            for (ColumnMapper<T> mapper : mappers) {
                Cell cell = metadataRow.createCell(cellCount++, CellType.STRING);
                cell.setCellValue(mapper.getFieldName());
            }
            // Hide metadata row
            metadataRow.setZeroHeight(true);
        }

        // This is title row
        if (!table.isNoHeader()) {
            Row headerRow = getRowAt(sheet, rowAt++);
            CellStyle defaultHeaderStyle = cachedStyles.accumulate(internalHeaderStyle);
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

                Object cellValue = mapper.retrieveValue(object, table.getTClass());

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
                            mapper.setStyle(DefaultStyle.DATE);
                        } else if (!mapper.getStyle().isDate()) {
                            // only allow direct accumulate style to .setStyle()
                            Style dateStyle = cachedStyles.unsafeAccumulate(mapper.getStyle(), DefaultStyle.DATE);
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
                Style rowStyle = table.applyConditionalRowStyle(object);
                CellStyle cellStyle = cachedStyles.accumulate(internalDataStyle, rowStyle, columnStyle, conditionalStyle);
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
                    if (table.isAutoResizeColumns()) {
                        sheet.autoSizeColumn(i, mapper.needMerged());
                    }
                }

            } // end of (mapper : mappers)
        } // end of (T object : data)
    }

    public ByteArrayInputStream exportToFile() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            out.close();
            System.out.println("An Excel file was successfully created.");
            return in;
        } catch (Exception e) {
            System.out.println("There some error writing excel file:");
            e.printStackTrace();
            return null;
        }
    }

    public void skipLines(int numberOfLines) {
        Sheet sheet = getLastSheet();
        int rowCount = getLastRowIndex(sheet);
        for (int i = 0; i < numberOfLines; i++) {
            sheet.createRow(rowCount++);
        }
    }

    public void writeAnywhere(Sheet sheet, String content, int rowAt, int colAt, int rowSpan, int colSpan, Style style) {
        Row row = getRowAt(sheet, rowAt);
        Cell cell = getCellAt(row, colAt);

        // Set core value
        cell.setCellValue(content);
        CellStyle cellStyle = cachedStyles.accumulate(dataStyle, style);
        cell.setCellStyle(cellStyle);

        // Validate span values
        if (rowSpan < 1) {
            rowSpan = 1;
        }
        if (colSpan < 1) {
            colSpan = 1;
        }
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
