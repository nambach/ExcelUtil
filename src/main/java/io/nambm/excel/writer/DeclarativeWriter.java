package io.nambm.excel.writer;

import io.nambm.excel.SimpleWriter;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public class DeclarativeWriter implements SimpleWriter {

    public DeclarativeWriter() {
    }

    @Override
    public <T> ByteArrayInputStream write(Collection<T> data, Table<T> table) {
        BaseWriter writer = new BaseWriter(table);
        Sheet sheet = writer.createNewSheet("Sheet 1");
        int rowIndex = writer.getLastRowIndex(sheet);
        writer.writeDataIntoSheet(sheet, data, table, rowIndex, 0);
        return writer.exportToFile();
    }

    @Override
    public <T> ByteArrayInputStream writeTemplate(Table<T> table) {
        table.setReuseForImport(true);
        BaseWriter writer = new BaseWriter(table);
        Sheet sheet = writer.createNewSheet("Sheet 1");
        int rowIndex = writer.getLastRowIndex(sheet);
        writer.writeDataIntoSheet(sheet, null, table, rowIndex, 0);
        return writer.exportToFile();
    }
}
