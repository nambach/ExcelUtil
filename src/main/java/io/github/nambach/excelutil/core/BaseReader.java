package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ListUtil;
import io.github.nambach.excelutil.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


class BaseReader extends BaseEditor {

    public BaseReader() {
    }

    public <T> Result<T> readSheet(Sheet sheet, ReaderConfig<T> baseConfig, int rowAt, int colAt) {
        Result<T> result = new Result<>(baseConfig.getTClass());
        try {
            if (sheet.getPhysicalNumberOfRows() == 0) {
                return result;
            }

            ReaderConfig<T> config = baseConfig.translate(rowAt, colAt);
            Map<Integer, Handlers<T>> handlerMap = config.getHandlerMap();

            int hasTitle = 0;
            Map<Integer, String> titleMap = new HashMap<>();
            int rowTitleIndex = config.getTitleRowIndex();
            if (rowTitleIndex >= 0) {
                hasTitle = 1;
                Row titleRow = sheet.getRow(rowTitleIndex);
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

                    Handlers<T> handlers = handlerMap.get(colIndex);
                    if (handlers == null) {
                        continue;
                    }

                    String colTitle = titleMap.get(colIndex);

                    // Wrap cell
                    ReaderCell readerCell = new ReaderCell(cell, colTitle);

                    // process raw only one time
                    boolean rawReached = false;

                    // iterate all handlers registered by user
                    for (Handler<T> handler : handlers) {
                        // Prepare ingredients
                        String fieldName = handler.getFieldName();
                        PropertyDescriptor pd = ReflectUtil.getField(fieldName, config.getTClass());
                        BiConsumer<T, ReaderCell> handle = handler.getHandler();

                        if (pd != null) {
                            Object cellValue = handleField(pd, object, readerCell);

                            // handle validation
                            if (handler.needValidation()) {
                                List<String> errorMessage = handler.validate(cellValue);
                                if (ListUtil.hasMember(errorMessage)) {
                                    result.addError(rowIndex, fieldName, errorMessage);
                                    if (baseConfig.isEarlyExist()) {
                                        return result;
                                    }
                                }
                            }
                        } else if (handle != null) {
                            handle.accept(object, readerCell);

                            // write error
                            if (readerCell.hasError()) {
                                result.addError(rowIndex, null, readerCell.getError());
                                readerCell.clearError();
                                if (readerCell.earlyExist) {
                                    return result;
                                }
                            }
                        } else if (!rawReached) {
                            rawReached = true;
                            handleOther(raw, cell, fieldName, colTitle);
                        }
                    }
                }

                // handle before adding new item
                ReaderRow readerRow = new ReaderRow();
                if (config.needHandleBeforeAdd()) {
                    config.getBeforeAddItemHandle().accept(object, readerRow);
                }

                // write error
                if (readerRow.hasError()) {
                    result.addError(rowIndex, null, readerRow.error);
                    readerRow.clearError();
                    if (readerRow.earlyExist) {
                        return result;
                    }
                }

                // add item
                if (!readerRow.skipThisObject) {
                    result.addRaw(raw);
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    private <T> Object handleField(PropertyDescriptor pd, T object, ReaderCell cell) {
        Method setter = pd.getWriteMethod();
        try {
            Object cellValue = null;
            switch (ReflectUtil.checkType(pd.getPropertyType())) {
                case STRING:
                    cellValue = cell.readString();
                    break;
                case LONG:
                    cellValue = cell.readLong();
                    break;
                case INTEGER:
                    cellValue = cell.readInt();
                    break;
                case DOUBLE:
                    cellValue = cell.readDouble();
                    break;
                case FLOAT:
                    cellValue = cell.readFloat();
                    break;
                case BOOLEAN:
                    cellValue = cell.readBoolean();
                    break;
                case DATE:
                    cellValue = cell.readDate();
                    break;
                default:
                    return null;
            }
            setter.invoke(object, cellValue);
            return cellValue;
        } catch (Exception e) {
            System.out.println("Error while invoking setter: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private <T> void handleOther(Raw<T> raw, Cell cell, String fieldName, String colTitle) {
        String key = fieldName != null ? fieldName :
                     colTitle != null ? colTitle :
                     cell.getColumnIndex() + "";
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
