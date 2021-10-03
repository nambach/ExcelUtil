package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.validator.ObjectError;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class RowError {

    private final int index;
    @Setter
    private String customError;
    @Setter
    private ObjectError objectError;

    public RowError(int index, Class<?> clazz) {
        this.index = index;
        if (clazz != null) {
            objectError = new ObjectError(clazz);
        }
    }

    void appendError(String field, List<String> messages) {
        objectError.appendError(field, messages);
    }

    public int getExcelIndex() {
        return index + 1;
    }

    public String getRowString() {
        return "Row " + (index + 1);
    }

    public String getMessage() {
        if (customError != null) {
            return customError;
        }
        if (objectError != null) {
            return objectError.getMessage();
        }
        return "";
    }

    public String getInlineMessage() {
        if (customError != null) {
            return customError;
        }
        if (objectError != null) {
            return objectError.getInlineMessage();
        }
        return "";
    }

    @Override
    public String toString() {
        return getRowString() + ": " + getInlineMessage();
    }
}
