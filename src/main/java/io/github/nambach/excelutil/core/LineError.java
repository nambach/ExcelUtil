package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.validator.Error;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class LineError {
    private final Error objectError;

    @Getter
    private final int index;

    public LineError(int index, Class<?> clazz) {
        this.index = index;
        this.objectError = new Error(clazz);
    }

    void appendError(String field, List<String> messages) {
        objectError.appendError(field, messages);
    }

    public Error getDetailedError() {
        return this.objectError;
    }

    public String getLine() {
        return "Line " + (index + 1);
    }

    public String getMessage() {
        return objectError.toString();
    }

    public String getInlineMessage() {
        return objectError.stream().map(Error.TypeError::toInlineMessage).collect(Collectors.joining("; "));
    }

    @Override
    public String toString() {
        return getLine() + ": " + getInlineMessage();
    }
}
