package io.nambm.excel.writer;

import io.nambm.excel.style.Style;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Function;

public class Editor {
    private final BaseWriter writer;
    private final Pointer pointer = new Pointer();
    private final Pointer pivot = new Pointer();
    private Style tempStyle;

    public Editor() {
        this.writer = new BaseWriter();
    }

    public Editor(InputStream stream) {
        this.writer = new BaseWriter(stream);
    }

    public Editor useStyle(Style style) {
        this.tempStyle = style;
        return this;
    }

    private void resetPointer() {
        pointer.update(writer.getNextRowIndex(), 0);
        pivot.sync(pointer);
    }

    public Editor goToSheet(int index) {
        writer.setSheetAt(index);
        resetPointer();
        return this;
    }

    public Editor goToSheet(String sheetName) {
        writer.setActiveSheet(sheetName);
        resetPointer();
        return this;
    }

    public Editor nextSheet() {
        writer.setNextSheet();
        resetPointer();
        return this;
    }

    public Editor goToCell(String address) {
        pointer.update(address);
        pivot.sync(pointer);
        return this;
    }

    public Editor goToCell(int row, int col) {
        pointer.update(row, col);
        pivot.sync(pointer);
        return this;
    }

    public Editor moveRight() {
        pointer.jumpRight(pivot);
        pivot.sync(pointer);
        return this;
    }

    public Editor moveRight(int steps) {
        if (steps > 0) {
            moveRight();
            pointer.moveRight(steps - 1);
            pivot.sync(pointer);
        }
        return this;
    }

    public Editor moveDown() {
        pointer.jumpDown(pivot);
        pivot.sync(pointer);
        return this;
    }

    public Editor moveDown(int steps) {
        if (steps > 0) {
            moveDown();
            pointer.moveDown(steps - 1);
            pivot.sync(pointer);
        }
        return this;
    }

    public Editor enter() {
        pointer.update(writer.getNextRowIndex(), 0);
        pivot.sync(pointer);
        return this;
    }

    public Editor enter(int steps) {
        if (steps > 0) {
            this.enter();
            pointer.moveDown(steps - 1);
            pivot.sync(pointer);
        }
        return this;
    }

    public Editor writeCell(Function<CellInfo, CellInfo> f) {
        CellInfo cell = new CellInfo(pointer, tempStyle);
        f.apply(cell);
        writer.writeCellInfo(cell);

        // update pivot
        pivot.moveRight(cell.getColSpan() - 1);
        pivot.moveDown(cell.getRowSpan() - 1);
        return this;
    }

    public Editor writeTemplate(Template template) {
        writer.writeTemplate(template);

        // update pointer
        int minRow = template.getCells().stream().map(CellInfo::getRowAt)
                             .reduce(Math::min).orElse(0);
        int minCol = template.getCells().stream().map(CellInfo::getColAt)
                             .reduce(Math::min).orElse(0);
        pointer.update(minRow, minCol);

        // update pivot
        int maxRow = template.getCells().stream().map(c -> c.getRowAt() + c.getRowSpan() - 1)
                             .reduce(Math::max).orElse(0);
        int maxCol = template.getCells().stream().map(c -> c.getColAt() + c.getColSpan() - 1)
                             .reduce(Math::max).orElse(0);
        pivot.update(maxRow, maxCol);
        return this;
    }

    public <T> Editor writeData(DataTemplate<T> template, Collection<T> data) {
        writer.writeData(template, data, pointer.getRow(), pointer.getCol());

        // update pivot
        pivot.moveRight(template.getMappers().size() - 1);
        int headerRows = (template.isNoHeader() ? 0 : 1) + (template.isReuseForImport() ? 1 : 0);
        pivot.moveDown(data.size() - 1 + headerRows);

        return this;
    }

    public ByteArrayInputStream exportToFile() {
        return writer.exportToFile();
    }

    public Editor config(Function<Config, Config> f) {
        f.apply(new Config(this));
        return this;
    }

    public static class Config {
        private final Editor editor;

        public Config(Editor editor) {
            this.editor = editor;
        }

        public Config freeze(int rows, int cols) {
            if (rows < 0) {
                rows = 0;
            }
            if (cols < 0) {
                cols = 0;
            }
            Sheet sheet = editor.writer.currentSheet;
            sheet.createFreezePane(cols, rows);
            return this;
        }
    }
}
