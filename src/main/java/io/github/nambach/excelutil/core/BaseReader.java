package io.nambm.excel.core;

import io.nambm.excel.model.Raw;
import io.nambm.excel.model.func.ConsumerBoolean;
import io.nambm.excel.model.func.ConsumerChecker;
import io.nambm.excel.model.func.ConsumerDate;
import io.nambm.excel.model.func.ConsumerDouble;
import io.nambm.excel.model.func.ConsumerString;
import io.nambm.excel.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


class BaseReader {

    public BaseReader() {
    }

    public <T> List<Raw<T>> readSheet(Sheet sheet, ReaderConfig<T> config) {
        try {
            List<Raw<T>> result = new LinkedList<>();

            // sheet.getFirstRowNum() return -1 if rows is empty
            int firstRowNum = sheet.getFirstRowNum();
            if (firstRowNum == -1) {
                return new LinkedList<>();
            }

            Map<Integer, String> fieldNames = config.getFieldNameByColIndex();
            int metadataRowIndex = config.getMetadataRowIndex();
            if (metadataRowIndex >= 0) {
                Row metadataRow = sheet.getRow(metadataRowIndex);
                for (Cell cell : metadataRow) {
                    fieldNames.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
            }

            Map<Integer, String> columnTitles = new HashMap<>();
            int rowTitleIndex = config.getTitleRowIndex();
            if (rowTitleIndex >= 0 && rowTitleIndex != metadataRowIndex) {
                Row titleRow = sheet.getRow(rowTitleIndex);
                for (Cell cell : titleRow) {
                    columnTitles.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
            }

            for (Row currentRow : sheet) {
                if (currentRow.getRowNum() < config.getDataFromRow()) {
                    continue;
                }

                T object = config.getTClass().newInstance();
                Raw<T> raw = new Raw<>();
                raw.setData(object);

                for (Cell cell : currentRow) {
                    int colIndex = cell.getColumnIndex();
                    String fieldName = fieldNames.get(colIndex);
                    String colTitle = columnTitles.get(colIndex);

                    BiConsumer<T, ?> customSetter = config.getConsumer(colIndex, fieldName, colTitle);
                    PropertyDescriptor pd = ReflectUtil.getField(fieldName, config.getTClass());

                    if (customSetter != null) {
                        handleConsumer(customSetter, object, cell);
                    } else if (pd != null) {
                        handleField(pd, object, cell);
                    } else {
                        handleOther(raw, cell, fieldName, colTitle);
                    }
                }

                result.add(raw);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    private <T> void handleConsumer(BiConsumer<T, ?> consumer, T object, Cell cell) {
        switch (ConsumerChecker.determineSecondParamType(consumer)) {
            case STRING:
                if (cell.getCellType() != CellType.STRING) {
                    cell.setCellType(CellType.STRING);
                }
                ReflectUtil.safeConsume((ConsumerString<T>) consumer,
                                        object, cell.getStringCellValue());
                break;
            case DOUBLE:
                if (cell.getCellType() == CellType.NUMERIC) {
                    ReflectUtil.safeConsume((ConsumerDouble<T>) consumer,
                                            object, cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                if (cell.getCellType() == CellType.BOOLEAN) {
                    ReflectUtil.safeConsume((ConsumerBoolean<T>) consumer,
                                            object, cell.getBooleanCellValue());
                }
                break;
            case DATE:
                ReflectUtil.safeConsume((ConsumerDate<T>) consumer,
                                        object, cell.getDateCellValue());
        }
    }

    private <T> void handleField(PropertyDescriptor pd, T object, Cell cell) {
        Method setter = pd.getWriteMethod();
        try {
            Object cellValue = null;
            switch (ReflectUtil.checkType(pd.getPropertyType())) {
                case STRING:
                    if (cell.getCellType() != CellType.STRING) {
                        cell.setCellType(CellType.STRING);
                    }
                    cellValue = cell.getStringCellValue();
                    break;
                case LONG:
                    if (cell.getCellType() == CellType.NUMERIC) {
                        cellValue = (long) cell.getNumericCellValue();
                    }
                    break;
                case INTEGER:
                    if (cell.getCellType() == CellType.NUMERIC) {
                        cellValue = (int) cell.getNumericCellValue();
                    }
                    break;
                case DOUBLE:
                    if (cell.getCellType() == CellType.NUMERIC) {
                        cellValue = cell.getNumericCellValue();
                    }
                    break;
                case FLOAT:
                    if (cell.getCellType() == CellType.NUMERIC) {
                        cellValue = (float) cell.getNumericCellValue();
                    }
                    break;
                case BOOLEAN:
                    if (cell.getCellType() == CellType.BOOLEAN) {
                        cellValue = cell.getBooleanCellValue();
                    }
                    break;
                case DATE:
                    cellValue = cell.getDateCellValue();
                    break;
                default:
                    return;
            }
            setter.invoke(object, cellValue);
        } catch (Exception e) {
            System.out.println("Error while invoking setter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private <T> void handleOther(Raw<T> raw, Cell cell, String colId, String colTitle) {
        String key = colId != null ? colId
                                   : colTitle != null ? colTitle
                                                      : cell.getColumnIndex() + "";
        switch (cell.getCellType()) {
            case STRING:
            case FORMULA:
                raw.getOtherData().put(key, cell.getStringCellValue());
                break;
            case NUMERIC:
                raw.getOtherData().put(key, cell.getNumericCellValue());
                break;
            case BOOLEAN:
                raw.getOtherData().put(key, cell.getBooleanCellValue());
                break;
        }
    }

    public <T> List<Raw<T>> readSingleSheet(InputStream inputStream, ReaderConfig<T> config) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            int sheetIndex = config.getSheetIndexes().stream().findFirst().orElse(0);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            return readSheet(sheet, config);
        } catch (IOException e) {
            System.out.println("Error while loading excel file: " + e.getMessage());
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public <T> Map<String, List<Raw<T>>> readMultipleSheets(InputStream inputStream, ReaderConfig<T> config) {
        Map<String, List<Raw<T>>> result = new LinkedHashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            for (Integer sheetIndex : config.getSheetIndexes()) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();
                List<Raw<T>> rawList = readSheet(sheet, config);
                result.put(sheetName, rawList);
            }
            return result;
        } catch (IOException e) {
            System.out.println("Error while loading excel file: " + e.getMessage());
            e.printStackTrace();
            return result;
        }
    }

    public <T> Map<String, List<Raw<T>>> readAllSheets(InputStream inputStream, ReaderConfig<T> config) {
        Map<String, List<Raw<T>>> result = new LinkedHashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                List<Raw<T>> rawList = readSheet(sheet, config);
                result.put(sheetName, rawList);
            }
            return result;
        } catch (IOException e) {
            System.out.println("Error while loading excel file: " + e.getMessage());
            e.printStackTrace();
            return result;
        }
    }
}
