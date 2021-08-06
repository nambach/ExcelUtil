package io.github.nambach.excelutil.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Function;

public class SequentialWriter implements AutoCloseable {
    private final Editor editor;

    public SequentialWriter() {
        editor = new Editor();
    }

    public SequentialWriter(InputStream stream) {
        editor = new Editor(stream);
    }

    public void createNewSheet(String sheetName) {
        editor.goToSheet(sheetName);
    }

    public <T> void writeData(DataTemplate<T> template, Collection<T> data) {
        editor.writeData(template, data)
              .enter();
    }

    public void writeTemplate(Template template) {
        editor.writeTemplate(template)
              .enter();
    }

    public void writeLine(int indent, Function<WriterCell, WriterCell> detail) {
        editor.moveRight(indent);
        editor.writeCell(detail)
              .enter();
    }

    public void skipLines(int lines) {
        editor.enter(lines);
    }

    public void freeze(int rows, int cols) {
        editor.configSheet(cf -> cf.freeze(rows, cols));
    }

    public ByteArrayInputStream exportToFile() {
        return editor.exportToFile();
    }

    @Override
    public void close() {
        this.editor.close();
    }
}
