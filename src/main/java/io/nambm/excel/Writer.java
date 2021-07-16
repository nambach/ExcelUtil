package io.nambm.excel;

import io.nambm.excel.style.Style;
import io.nambm.excel.writer.Table;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public interface Writer {
    void createNewSheet(String sheetName);

    <T> void writeData(Collection<T> data, Table<T> table);

    <T> void writeData(Collection<T> data, Table<T> table, int colAt);

    ByteArrayInputStream exportToFile();

    void resetAll();

    void skipLines(int numberOfLines);

    void writeAnywhere(String content, int rowAt, int colAt);

    void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan);

    void freeze(int rows, int cols);

    void setCurrentStyle(Style style, boolean accumulate);

}
