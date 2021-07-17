package io.nambm.excel.writer;

import io.nambm.excel.Writer;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

public class WriterImpl implements Writer {
    private BaseWriter writer;

    public WriterImpl() {
        writer = new BaseWriter();
    }

    public WriterImpl(InputStream stream) {
        writer = new BaseWriter(stream);
    }

    @Override
    public void newWorkbook() {
        writer = new BaseWriter();
    }

    @Override
    public void loadWorkbook(InputStream stream) {
        writer = new BaseWriter(stream);
    }

    @Override
    public void createNewSheet(String sheetName) {
        writer.createNewSheet(sheetName);
    }

    @Override
    public <T> void writeData(DataTemplate<T> table, Collection<T> data) {
        Sheet sheet = writer.getLastSheet();
        writer.writeDataIntoSheet(sheet, table, data);
    }

    @Override
    public void writeTemplate(Template template) {
        Sheet sheet = writer.getLastSheet();
        writer.writeTemplate(sheet, template);
    }

    @Override
    public void freeze(int rows, int cols) {
        if (rows < 0) {
            rows = 0;
        }
        if (cols < 0) {
            cols = 0;
        }
        Sheet sheet = writer.getLastSheet();
        sheet.createFreezePane(cols, rows);
    }

    @Override
    public ByteArrayInputStream exportToFile() {
        return writer.exportToFile();
    }
}
