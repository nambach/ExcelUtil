package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.validator.Error;
import io.github.nambach.excelutil.validator.Validator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


class BaseReader implements BaseEditor {

    public BaseReader() {
    }

    public <T> Result<T> readSheet(Sheet sheet, ReaderConfig<T> baseConfig, int rowAt, int colAt) {
        Result<T> result = new Result<>(baseConfig.getTClass());
        try {
            if (sheet.getPhysicalNumberOfRows() == 0) {
                return result;
            }

            ReaderConfig<T> config = baseConfig.translate(rowAt, colAt);
            HandlerMap<T> handlerMap = config.getHandlerMap();

            int hasTitle = 0;
            Map<Integer, String> titleMap = new HashMap<>();
            int rowTitleIndex = config.getTitleRowIndex();
            if (rowTitleIndex >= 0) {
                hasTitle = 1;
                Row titleRow = sheet.getRow(rowTitleIndex);
                if (titleRow == null) {
                    throw new RuntimeException("Title row at index " + rowTitleIndex + " not found");
                }
                for (Cell cell : titleRow) {
                    if (cell.getColumnIndex() < colAt) {
                        continue;
                    }
                    titleMap.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
            }

            for (Row currentRow : sheet) {
                int rowIndex = currentRow.getRowNum();
                if (rowIndex < rowAt + hasTitle) {
                    continue;
                }

                T object;
                try {
                    object = config.getTClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new Exception("Please provide a no argument constructor for class " + config.getTClass().getName());
                }

                Raw<T> raw = new Raw<>();
                raw.setData(object);

                for (Cell cell : currentRow) {
                    int colIndex = cell.getColumnIndex();
                    if (colIndex < colAt) {
                        continue;
                    }

                    String colTitle = titleMap.get(colIndex);

                    HandlerMap.Handlers<T> handlers = handlerMap.get(colIndex, colTitle);
                    if (handlers == null) {
                        continue;
                    }

                    // Wrap cell
                    ReaderCell readerCell = new ReaderCell(cell, colTitle, config, result);

                    // iterate all handlers registered by user
                    for (Handler<T> handler : handlers) {
                        // Prepare ingredients
                        BiConsumer<T, ReaderCell> handle = handler.getHandler();

                        // Do validation first
                        if (handler.needValidation()) {
                            readerCell.validate(handler.getTypeValidator());
                        }

                        if (handle != null) {
                            handle.accept(object, readerCell);
                        }

                        // Post-check validation
                        if (readerCell.isExitNow()) {
                            return result;
                        }
                    }

                    // process raw only one time
                    handleOther(raw, cell, colTitle);
                }

                // handle before adding new item
                ReaderRow readerRow = new ReaderRow(currentRow, config, result);
                if (config.needHandleBeforeAdd()) {
                    config.getBeforeAddItemHandle().accept(object, readerRow);
                }

                // validate object
                Validator<T> validator = config.getValidator();
                if (validator != null) {
                    Error error = validator.validate(object);
                    if (error.hasErrors()) {
                        readerRow.setError(error.toString());
                        if (config.isEarlyExit()) {
                            readerRow.terminateNow();
                        }
                    }
                }

                // Post-check validation
                if (readerRow.isExitNow()) {
                    return result;
                }

                // add item
                if (!readerRow.isSkipThisObject()) {
                    result.addRaw(raw);
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    private <T> void handleOther(Raw<T> raw, Cell cell, String colTitle) {
        String key = colTitle != null
                     ? colTitle
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
}
