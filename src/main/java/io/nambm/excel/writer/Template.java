package io.nambm.excel.writer;

import io.nambm.excel.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Template implements WriterTemplate, Iterable<CellInfo> {
    private final LinkedList<CellInfo> cells = new LinkedList<>();
    private final Pointer pointer = new Pointer();
    private Style tempStyle;

    Template() {
    }

    public static Template builder() {
        return new Template();
    }

    public Template makeCopy() {
        Template clone = new Template();
        clone.cells.addAll(this.cells);
        clone.pointer.sync(this.pointer);
        clone.tempStyle = this.tempStyle;
        return clone;
    }

    public Template at(int rowAt, int colAt) {
        pointer.update(rowAt, colAt);
        return this;
    }

    public Template at(String address) {
        pointer.update(address);
        return this;
    }

    public Template moveRight() {
        this.pointer.moveRight();
        return this;
    }

    public Template moveDown() {
        this.pointer.moveDown();
        return this;
    }

    public Template cell(Function<CellInfo, CellInfo> builder) {
        CellInfo cell = new CellInfo(pointer, tempStyle);
        builder.apply(cell);
        cells.add(cell);
        return this;
    }

    public Template right(Function<CellInfo, CellInfo> builder) {
        pointer.moveRight();
        return cell(builder);
    }

    public Template down(Function<CellInfo, CellInfo> builder) {
        pointer.moveDown();
        return cell(builder);
    }

    public Template useStyle(Style style) {
        this.tempStyle = style;
        return this;
    }

    @Override
    public Iterator<CellInfo> iterator() {
        return cells.iterator();
    }

    public ByteArrayInputStream getFile() {
        BaseWriter writer = new BaseWriter();
        writer.writeTemplate(this);
        return writer.exportToFile();
    }
}
