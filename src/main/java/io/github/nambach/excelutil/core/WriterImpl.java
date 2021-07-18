package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.Writer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Function;

public class WriterImpl implements Writer {
    private final Editor editor;

    public WriterImpl() {
        editor = new Editor();
    }

    public WriterImpl(InputStream stream) {
        editor = new Editor(stream);
    }

    @Override
    public void createNewSheet(String sheetName) {
        editor.goToSheet(sheetName);
    }

    @Override
    public <T> void writeData(DataTemplate<T> template, Collection<T> data) {
        editor.writeData(template, data)
              .enter();
    }

    @Override
    public void writeTemplate(Template template) {
        editor.writeTemplate(template)
              .enter();
    }

    @Override
    public void writeLine(int indent, Function<CellInfo, CellInfo> detail) {
        for (int i = 0; i < indent; i++) {
            editor.moveRight();
        }
        editor.writeCell(detail)
              .enter();
    }

    @Override
    public void skipLines(int lines) {
        editor.enter(lines);
    }

    @Override
    public void freeze(int rows, int cols) {
        editor.config(cf -> cf.freeze(rows, cols));
    }

    @Override
    public ByteArrayInputStream exportToFile() {
        return editor.exportToFile();
    }
}
