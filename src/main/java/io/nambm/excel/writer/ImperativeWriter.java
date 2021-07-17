package io.nambm.excel.writer;

import io.nambm.excel.Writer;
import io.nambm.excel.style.Style;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public class ImperativeWriter implements Writer {
    private BaseWriter writer;
    private Style currentStyle;

    public ImperativeWriter() {
        writer = new BaseWriter(null);
    }

    @Override
    public void createNewSheet(String sheetName) {
        writer.createNewSheet(sheetName);
    }

    @Override
    public <T> void writeData(Collection<T> data, Table<T> table) {
        Sheet sheet = writer.getLastSheet();
        int rowIndex = writer.getLastRowIndex(sheet);
        writer.writeDataIntoSheet(sheet, data, table, rowIndex, 0);
    }

    @Override
    public <T> void writeData(Collection<T> data, Table<T> table, int rowAt, int colAt) {
        Sheet sheet = writer.getLastSheet();
        writer.writeDataIntoSheet(sheet, data, table, rowAt, colAt);
    }

    @Override
    public void skipLines(int numberOfLines) {
        writer.skipLines(numberOfLines);
    }

    @Override
    public void writeLine(String content) {
        this.writeLine(content, 0, 0, currentStyle);
    }

    @Override
    public void writeLine(String content, int colAt, int colSpan) {
        this.writeLine(content, colAt, colSpan, currentStyle);
    }

    @Override
    public void writeLine(String content, int colAt, int colSpan, Style style) {
        Sheet sheet = writer.getLastSheet();
        int rowIndex = writer.getLastRowIndex(sheet);
        writer.writeAnywhere(sheet, content, rowIndex, colAt, 0, colSpan, style);
    }

    @Override
    public void writeAnywhere(String content, int rowAt, int colAt) {
        this.writeAnywhere(content, rowAt, colAt, 0, 0, currentStyle);
    }

    @Override
    public void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan) {
        this.writeAnywhere(content, rowAt, colAt, rowSpan, colSpan, currentStyle);
    }

    @Override
    public void writeAnywhere(String content, int rowAt, int colAt, int rowSpan, int colSpan, Style style) {
        Sheet sheet = writer.getLastSheet();
        writer.writeAnywhere(sheet, content, rowAt, colAt, rowSpan, colSpan, style);
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
    public void setCurrentStyle(Style style, Style... accumulate) {
        this.currentStyle = style;
        if (accumulate == null || accumulate.length == 0) {
            return;
        }
        for (Style subStyle : accumulate) {
            this.currentStyle = writer.cachedStyles.unsafeAccumulate(this.currentStyle, subStyle);
        }
    }

    @Override
    public ByteArrayInputStream exportToFile() {
        return writer.exportToFile();
    }

    @Override
    public void resetAll() {
        writer = new BaseWriter(null);
    }
}
