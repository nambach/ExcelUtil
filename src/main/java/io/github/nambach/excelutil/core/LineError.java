package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.validator.Error;
import lombok.Getter;

import java.util.stream.Collectors;

public class LineError {
    @Getter
    private final int index;
    final Error objectError;

    public LineError(int index, Error objectError) {
        this.index = index;
        this.objectError = objectError;
    }

    public String getMessage() {
        return objectError.toString();
    }

    public String getInlineMessage() {
        return objectError.stream().map(Error.TypeError::getInlineMessage).collect(Collectors.joining("; "));
    }

    @Override
    public String toString() {
        return String.format("Line %d: %s", (index + 1), getInlineMessage());
    }
}
