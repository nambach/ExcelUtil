package io.nambm.excel;

import io.nambm.excel.writer.DataTemplate;
import io.nambm.excel.writer.Template;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

public interface Writer {
    void newWorkbook();

    void loadWorkbook(InputStream stream);

    void createNewSheet(String sheetName);

    <T> void writeData(DataTemplate<T> table, Collection<T> data);

    void writeTemplate(Template template);

    void freeze(int rows, int cols);

    ByteArrayInputStream exportToFile();
}
