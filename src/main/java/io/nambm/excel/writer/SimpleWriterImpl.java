package io.nambm.excel.writer;

import io.nambm.excel.SimpleWriter;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public class SimpleWriterImpl implements SimpleWriter {

    public SimpleWriterImpl() {
    }

    @Override
    public <T> ByteArrayInputStream write(Collection<T> data, Table<T> table) {
        BaseWriter writer = new BaseWriter(table);
        writer.createNewSheet("Sheet 1");
        writer.writeDataIntoSheet(data, table);
        return writer.exportToFile();
    }

    @Override
    public <T> ByteArrayInputStream writeTemplate(Table<T> table) {
        table.setReuseForImport(true);
        BaseWriter writer = new BaseWriter(table);
        writer.createNewSheet("Sheet 1");
        writer.writeDataIntoSheet(null, table);
        return writer.exportToFile();
    }
}
