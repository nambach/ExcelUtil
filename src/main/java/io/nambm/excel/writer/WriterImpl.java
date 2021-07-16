package io.nambm.excel.writer;

import io.nambm.excel.Writer;
import io.nambm.excel.style.Style;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public class WriterImpl implements Writer {
    private BaseWriter writer;
    private Style currentStyle;

    public WriterImpl() {
        writer = new BaseWriter(null);
    }

    @Override
    public void createNewSheet(String sheetName) {
        writer.createNewSheet(sheetName);
    }

    @Override
    public <T> void writeData(Collection<T> data, Table<T> table) {
        writer.writeDataIntoSheet(data, table);
    }

    @Override
    public <T> void writeData(Collection<T> data, Table<T> table, int colAt) {
        writer.writeDataIntoSheet(data, table, colAt);
    }

    @Override
    public ByteArrayInputStream exportToFile() {
        return writer.exportToFile();
    }

    @Override
    public void resetAll() {
        writer = new BaseWriter(null);
    }

    @Override
    public void skipLines(int numberOfLines) {
        writer.skipLines(numberOfLines);
    }

    @Override
    public void writeAnywhere(String content, int rowAt, int colAt) {
        writer.writeAnywhere(content, rowAt, colAt, 0, 0, currentStyle);
    }

    @Override
    public void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan) {
        writer.writeAnywhere(content, rowAt, colAt, rowSpan, colSpan, currentStyle);
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
    public void setCurrentStyle(Style style, boolean accumulate) {
        if (accumulate) {
            this.currentStyle = writer.cachedStyles.unsafeAccumulate(this.currentStyle, style);
        } else {
            this.currentStyle = style;
        }
    }
}
