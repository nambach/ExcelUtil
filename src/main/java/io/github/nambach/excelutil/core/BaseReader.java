package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


class BaseReader extends BaseEditor {

    public BaseReader() {
    }

    public <T> Result<T> readSheet(Sheet sheet, ReaderConfig<T> baseConfig, int rowAt, int colAt) {
        Result<T> result = new Result<>();
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
                if (currentRow.getRowNum() < rowAt + hasTitle) {
                    continue;
                }

                T object = config.getTClass().newInstance();
                Raw<T> raw = new Raw<>();
                raw.setData(object);

                for (Cell cell : currentRow) {
                    int colIndex = cell.getColumnIndex();
                    if (colIndex < colAt) {
                        continue;
                    }

                    Handlers<T> handlers = handlerMap.get(colIndex);
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
                            handleField(pd, object, readerCell);
                        } else if (handle != null) {
                            handle.accept(object, readerCell);
                        } else if (!rawReached) {
                            rawReached = true;
                            handleOther(raw, cell, fieldName, colTitle);
                        }
                    }
                }

                result.addRaw(raw);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    private <T> void handleField(PropertyDescriptor pd, T object, ReaderCell cell) {
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
                    return;
            }
            setter.invoke(object, cellValue);
        } catch (Exception e) {
            System.out.println("Error while invoking setter: " + e.getMessage());
            e.printStackTrace();
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
