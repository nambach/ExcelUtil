package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.Style;

import java.util.Date;
import java.util.function.Function;

interface FreestyleWriter<T extends FreestyleWriter<T>> extends Navigation<T> {
    T useStyle(Style style);

    T applyStyle();

    T applyStyle(Style style);

    T applyStyle(Style style, String address);

    T applyStyle(Style style, String fromAddress, String toAddress);

    T writeCell(Function<WriterCell, WriterCell> builder);

    default T date(Date date) {
        return writeCell(c -> c.date(date));
    }

    default T number(double number) {
        return writeCell(c -> c.number(number));
    }

    default T text(String text) {
        return writeCell(c -> c.text(text));
    }
}
