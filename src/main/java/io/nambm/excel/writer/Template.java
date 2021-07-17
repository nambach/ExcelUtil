package io.nambm.excel.writer;

import io.nambm.excel.style.DefaultStyle;
import io.nambm.excel.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Template implements WriterTemplate, Iterable<CellInfo> {
    private Style baseStyle;
    private List<CellInfo> cells = new LinkedList<>();

    Template() {
        this.baseStyle = DefaultStyle.TIMES_NEW_ROMAN;
    }

    public static Template builder() {
        return new Template();
    }

    public Template cell(Function<CellInfo, CellInfo> builder) {
        Objects.requireNonNull(builder);
        CellInfo cell = new CellInfo();
        builder.apply(cell);
        cells.add(cell);
        return this;
    }

    public Template baseStyle(Style style) {
        this.baseStyle = style;
        return this;
    }

    @Override
    public Iterator<CellInfo> iterator() {
        return cells.iterator();
    }

    public ByteArrayInputStream getFile() {
        BaseWriter writer = new BaseWriter();
        Sheet sheet = writer.createNewSheet("Sheet 1");
        writer.writeTemplate(sheet, this);
        return writer.exportToFile();
    }
}
