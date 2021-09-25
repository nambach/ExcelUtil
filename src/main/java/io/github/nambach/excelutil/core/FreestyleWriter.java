package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.constraint.Constraint;
import io.github.nambach.excelutil.style.Style;

import java.util.Collection;
import java.util.Date;
import java.util.function.UnaryOperator;

interface FreestyleWriter<T extends FreestyleWriter<T>> extends Navigation<T> {
    T useStyle(Style style);

    T applyStyle();

    T applyStyle(Style style, String... address);

    T applyStyle(Style style, Collection<String> addresses);

    T applyConstraint(Constraint constraint, String... address);

    T applyConstraint(Constraint constraint, Collection<String> addresses);

    T writeComment(UnaryOperator<WriterComment> builder);

    default T comment(String comment) {
        return writeComment(c -> c.content(comment));
    }

    T writeCell(UnaryOperator<WriterCell> builder);

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
