package io.nambm.excel;

import io.nambm.excel.style.Style;
import io.nambm.excel.writer.Table;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public interface Writer {
    void createNewSheet(String sheetName);

    <T> void writeData(Collection<T> data, Table<T> table);

    <T> void writeData(Collection<T> data, Table<T> table, int rowAt, int colAt);

    void skipLines(int numberOfLines);

    void writeLine(String content);

    void writeLine(String content, int colAt, int colSpan);

    void writeLine(String content, int colAt, int colSpan, Style style);

    void writeAnywhere(String content, int rowAt, int colAt);

    void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan);

    void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan, Style style);

    void freeze(int rows, int cols);

    void setCurrentStyle(Style style, Style... accumulate);

    ByteArrayInputStream exportToFile();

    void resetAll();
}
