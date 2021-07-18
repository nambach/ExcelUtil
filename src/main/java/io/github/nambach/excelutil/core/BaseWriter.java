package io.nambm.excel.core;

import io.nambm.excel.style.CacheStyle;
import io.nambm.excel.style.DefaultStyle;
import io.nambm.excel.style.Style;
import io.nambm.excel.util.ReflectUtil;
import lombok.SneakyThrows;
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
import java.io.InputStream;
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
    Sheet currentSheet;

    BaseWriter() {
        this(null);
    }

    @SneakyThrows
    BaseWriter(InputStream stream) {
        XSSFWorkbook workbook = stream == null ? new XSSFWorkbook() : new XSSFWorkbook(stream);
        this.workbook = workbook;
        this.cachedStyles = new CacheStyle(workbook);

        if (stream != null) {
            setActiveSheetAsCurrent();
        }
    }

    int getNextRowIndex() {
        return currentSheet.getLastRowNum() + 1;
    }

    void setActiveSheetAsCurrent() {
        if (workbook.getNumberOfSheets() == 0) {
            setActiveSheet("Sheet 1");
        } else {
            int index = workbook.getActiveSheetIndex();
            currentSheet = workbook.getSheetAt(index);
        }
    }

    void setSheetAt(int index) {
        if (workbook.getNumberOfSheets() == 0) {
            setActiveSheet("Sheet 1");
        } else if (index < 0) {
            currentSheet = workbook.getSheetAt(0);
        } else if (index + 1 > workbook.getNumberOfSheets()) {
            currentSheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);
        } else {
            currentSheet = workbook.getSheetAt(index);
        }
    }

    void setNextSheet() {
        int index = workbook.getSheetIndex(currentSheet);
        setSheetAt(index + 1);
    }

    public void setActiveSheet(String sheetName) {
        currentSheet = workbook.getSheet(sheetName);
        if (currentSheet == null) {
            currentSheet = workbook.createSheet(sheetName);
        }
    }

    Row getRowAt(int rowAt) {
        Row row = currentSheet.getRow(rowAt);
        if (row == null) {
            row = currentSheet.createRow(rowAt);
        }
        return row;
    }

    Cell getCellAt(Row row, int colAt) {
        Cell cell = row.getCell(colAt);
        if (cell == null) {
            cell = row.createCell(colAt);
        }
        return cell;
    }

    Cell getCellAt(int rowAt, int colAt) {
        Row row = getRowAt(rowAt);
        return getCellAt(row, colAt);
    }

    public <T> void writeData(DataTemplate<T> template, Collection<T> data, int rowAt, int colAt) {
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
        if (template.isReuseForImport()) {
            Row metadataRow = getRowAt(rowAt++);
            int cellCount = colAt;
            for (ColumnMapper<T> mapper : mappers) {
                Cell cell = metadataRow.createCell(cellCount++, CellType.STRING);
                cell.setCellValue(mapper.getFieldName());
            }
            // Hide metadata row
            metadataRow.setZeroHeight(true);
        }

        // This is title row
        if (!template.isNoHeader()) {
            Row headerRow = getRowAt(rowAt++);
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

            Row dataRow = getRowAt(rowAt++);

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
                                tracker.handleMerge(currentSheet, cell);
                            }
                        } else {
                            // New value coped => finish last merge range
                            tracker.handleMerge(currentSheet, cell);
                            tracker.reset(currentValue, rowNum);
                        }
                    }
                }

                // Resize columns when last row reached
                if (objectCount == data.size()) {
                    if (template.isAutoResizeColumns()) {
                        currentSheet.autoSizeColumn(i, mapper.needMerged());
                    }
                }

            } // end of (mapper : mappers)
        } // end of (T object : data)
    }

    public void writeTemplate(Template template) {
        for (CellInfo cellInfo : template) {
            this.writeCellInfo(cellInfo);
        }
    }

    public void writeCellInfo(CellInfo cellInfo) {
        Row row = getRowAt(cellInfo.getRowAt());
        Cell cell = getCellAt(row, cellInfo.getColAt());

        // Set styles
        CellStyle cellStyle = cachedStyles.accumulate(cellInfo.getStyle());
        cell.setCellStyle(cellStyle);

        // Set core value
        if (cellInfo.getContent() != null) {
            cell.setCellValue(cellInfo.getContent());
        } else if (cellInfo.getValue() != null) {
            cell.setCellValue(cellInfo.getValue());
        } else if (cellInfo.getDate() != null) {
            cell.setCellValue(cellInfo.getDate());
            cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(cellInfo.getDatePattern()));
        }


        int rowAt = cellInfo.getRowAt();
        int colAt = cellInfo.getColAt();
        int rowSpan = cellInfo.getRowSpan();
        int colSpan = cellInfo.getColSpan();
        if (colSpan > 1 || rowSpan > 1) {

            // Spread style to whole range
            for (int colOffset = 0; colOffset < colSpan; colOffset++) {
                for (int rowOffset = 0; rowOffset < rowSpan; rowOffset++) {
                    if (rowOffset == 0 && colOffset == 0) {
                        continue;
                    }
                    Row currentRow = getRowAt(rowAt + rowOffset);
                    Cell currentCell = getCellAt(currentRow, colAt + colOffset);
                    currentCell.setCellStyle(cellStyle);
                }
            }

            currentSheet.addMergedRegion(new CellRangeAddress(rowAt, rowAt + rowSpan - 1,
                                                              colAt, colAt + colSpan - 1));
        }

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

}
