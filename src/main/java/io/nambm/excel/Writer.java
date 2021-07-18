package io.nambm.excel;

import io.nambm.excel.writer.CellInfo;
import io.nambm.excel.writer.DataTemplate;
import io.nambm.excel.writer.Template;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.function.Function;

public interface Writer {

    void createNewSheet(String sheetName);

    <T> void writeData(DataTemplate<T> template, Collection<T> data);

    void writeTemplate(Template template);

    void writeLine(int indent, Function<CellInfo, CellInfo> detail);

    void skipLines(int lines);

    void freeze(int rows, int cols);

    ByteArrayInputStream exportToFile();
}
