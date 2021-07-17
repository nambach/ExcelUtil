package io.nambm.excel.writer;

import io.nambm.excel.style.DefaultStyle;
import io.nambm.excel.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Template implements WriterTemplate, Iterable<CellInfo> {
    private Style baseStyle;
    private Style tempStyle;
    private LinkedList<CellInfo> cells = new LinkedList<>();
    private int[] pos = new int[]{0, 0};

    Template() {
        this.baseStyle = DefaultStyle.TIMES_NEW_ROMAN;
    }

    public static Template builder() {
        return new Template();
    }

    private CellInfo createCellInfo() {
        CellInfo cell = new CellInfo();
        if (tempStyle != null) {
            cell.getStyles().add(tempStyle);
        }
        return cell;
    }

    private void updatePosition(CellInfo cell) {
        pos[0] = cell.getRowAt();
        pos[1] = cell.getColAt();
    }

    @SneakyThrows
    public Template at(String address) {
        try {
            CellAddress cellAddress = new CellAddress(address);
            pos[0] = cellAddress.getRow();
            pos[1] = cellAddress.getColumn();
        } catch (Exception e) {
            throw new Exception("Error while parsing cell address: ", e);
        }
        return this;
    }

    public Template cell(Function<CellInfo, CellInfo> builder) {
        Objects.requireNonNull(builder);

        CellInfo cell = createCellInfo();
        cell.at(pos[0], pos[1]);
        builder.apply(cell);
        cells.add(cell);

        updatePosition(cell);
        return this;
    }

    public Template next(Function<CellInfo, CellInfo> builder) {
        Objects.requireNonNull(builder);

        CellInfo cell = createCellInfo();
        builder.apply(cell);

        int spanOffset = cells.isEmpty() ? 0 :
                         cells.getLast().getColSpan() - 1;
        cell.at(pos[0], pos[1] + 1 + spanOffset);
        cells.add(cell);

        updatePosition(cell);
        return this;
    }

    public Template down(Function<CellInfo, CellInfo> builder) {
        Objects.requireNonNull(builder);

        CellInfo cell = createCellInfo();
        builder.apply(cell);

        int spanOffset = cells.isEmpty() ? 0 :
                         cells.getLast().getRowSpan() - 1;
        cell.at(pos[0] + 1 + spanOffset, pos[1]);
        cells.add(cell);

        updatePosition(cell);
        return this;
    }

    public Template baseStyle(Style style) {
        this.baseStyle = style;
        return this;
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
        Sheet sheet = writer.createNewSheet("Sheet 1");
        writer.writeTemplate(sheet, this);
        return writer.exportToFile();
    }
}
