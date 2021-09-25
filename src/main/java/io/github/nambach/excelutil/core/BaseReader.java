package io.github.nambach.excelutil.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


class BaseReader implements BaseEditor {

    private <T> T createObject(Class<T> tClass) {
        try {
            return tClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Please provide a no argument constructor for class " + tClass.getName(), e);
        }
    }

    public <T> Result<T> readSheet(Sheet sheet, ReaderConfig<T> baseConfig, int rowAt, int colAt) {
        Result<T> result = new Result<>(baseConfig.getTClass());

        if (sheet.getPhysicalNumberOfRows() == 0) {
            return result;
        }

        // translate the original config to target coordinate
        ReaderConfig<T> config = baseConfig.translate(rowAt, colAt);
        HandlerMap<T> handlerMap = config.getHandlerMap();

        Map<Integer, String> titleMap = new HashMap<>();
        int rowTitleIndex = config.getTitleRowIndex();
        int rowTitleCount = rowTitleIndex >= 0 ? 1 : 0;
        readTitleRow(sheet, colAt, rowTitleIndex, titleMap);

        for (Row currentRow : sheet) {
            int rowIndex = currentRow.getRowNum();
            if (rowIndex < rowAt + rowTitleCount) {
                continue;
            }

            T object = createObject(config.getTClass());

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
                    BiConsumer<T, ReaderCell> handle = handler.getCoreHandler();

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

                // process raw if there is no handler
                if (handlers.isEmpty()) {
                    handleOther(raw, cell, colTitle);
                }
            }

            // handle before adding new item
            ReaderRow readerRow = new ReaderRow(currentRow, config, result);
            config.handleBeforeAdd(object, readerRow);

            // validate object
            config.validateObjectBeforeAdd(object, readerRow);
            if (readerRow.isExitNow()) {
                return result;
            }

            // add item
            if (!readerRow.isSkipThisObject()) {
                result.addRaw(raw);
            }
        }

        return result;
    }

    private void readTitleRow(Sheet sheet, int colAt, int titleIndex, Map<Integer, String> titleMap) {
        if (titleIndex < 0) return;

        Row titleRow = sheet.getRow(titleIndex);
        if (titleRow == null) {
            throw new RuntimeException("Title row at index " + titleIndex + " not found");
        }

        for (Cell cell : titleRow) {
            if (cell.getColumnIndex() < colAt) {
                continue;
            }
            titleMap.put(cell.getColumnIndex(), cell.getStringCellValue());
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
            default:
                break;
        }
    }
}
