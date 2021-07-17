package io.nambm.excel;

import io.nambm.excel.style.Style;
import io.nambm.excel.writer.DataTemplate;
import io.nambm.excel.writer.Template;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public interface Writer {
    void createNewSheet(String sheetName);

    <T> void writeData(DataTemplate<T> table, Collection<T> data);

    <T> void writeData(DataTemplate<T> table, Collection<T> data, int rowAt, int colAt);

    void writeTemplate(Template template);

    void skipLines(int numberOfLines);

    void writeLine(String content);

    void writeLine(String content, int colAt, int colSpan);

    void writeLine(String content, int colAt, int colSpan, Style style);

    void writeAnywhere(String content, int rowAt, int colAt);

    void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan);

    void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan, Style style);

    void freeze(int rows, int cols);

    void setCurrentStyle(Style style);

    ByteArrayInputStream exportToFile();

    void resetAll();
}
