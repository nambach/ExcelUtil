package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.validator.Error;
import lombok.Getter;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ReaderError extends ArrayList<ReaderError.Line> {

    public static class Line {
        @Getter
        private final int index;
        final Error objectError;

        public Line(int index, Error objectError) {
            this.index = index;
            this.objectError = objectError;
        }

        public String getMessage() {
            return objectError.toString();
        }

        public String getInlineMessage() {
            return objectError.stream().map(Error.Entry::inlineMessage).collect(Collectors.joining("; "));
        }

        @Override
        public String toString() {
            return String.format("Line %d: %s", (index + 1), getInlineMessage());
        }
    }
}
