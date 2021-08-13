package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.util.CellAddress;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Template implements Iterable<WriterCell>, FreestyleWriter<Template> {
    private final Map<String, WriterCell> cells = new HashMap<>();
    private final PointerNavigation navigation = new PointerNavigation();
    private Style tempStyle;

    Template() {
    }

    public static Template builder() {
        return new Template();
    }

    int[] getRowIndex() {
        Stream<Integer> rows = cells.values().stream().map(WriterCell::getRowAt);
        int min = rows.reduce(Math::min).orElse(0);
        int max = rows.reduce(Math::max).orElse(0);
        return new int[]{min, max};
    }

    int[] getColIndex() {
        Stream<Integer> cols = cells.values().stream().map(WriterCell::getColAt);
        int min = cols.reduce(Math::min).orElse(0);
        int max = cols.reduce(Math::max).orElse(0);
        return new int[]{min, max};
    }

    public Template makeCopy() {
        Template clone = new Template();
        clone.cells.putAll(this.cells);
        clone.navigation.sync(this.navigation);
        clone.tempStyle = this.tempStyle;
        return clone;
    }

    @Override
    public Template goToCell(String address) {
        navigation.goToCell(address);
        return this;
    }

    @Override
    public Template goToCell(int row, int col) {
        navigation.goToCell(row, col);
        return this;
    }

    @Override
    public Template next() {
        navigation.next();
        return this;
    }

    @Override
    public Template next(int steps) {
        navigation.next(steps);
        return this;
    }

    @Override
    public Template down() {
        navigation.down();
        return this;
    }

    @Override
    public Template down(int steps) {
        navigation.down(steps);
        return this;
    }

    @Override
    public Template enter() {
        navigation.enter();
        return this;
    }

    @Override
    public Template enter(int steps) {
        navigation.enter(steps);
        return this;
    }

    private WriterCell updateCell(CellAddress cellAddress, Function<WriterCell, WriterCell> builder) {
        String address = cellAddress.formatAsString();
        WriterCell current = cells.getOrDefault(address, new WriterCell(cellAddress, tempStyle));
        WriterCell newCell = builder.apply(current);
        cells.put(address, newCell);
        return newCell;
    }

    @Override
    public Template writeCell(Function<WriterCell, WriterCell> builder) {
        WriterCell cell = updateCell(navigation.getCellAddress(), builder);

        // update pivot
        navigation.updatePivotRight(cell.getColSpan() - 1);
        navigation.updatePivotDown(cell.getRowSpan() - 1);
        return this;
    }

    @Override
    public Template useStyle(Style style) {
        this.tempStyle = style;
        return this;
    }

    @Override
    public Template applyStyle() {
        updateCell(navigation.getCellAddress(), c -> c.style(tempStyle));
        return this;
    }

    @Override
    public Template applyStyle(Style style) {
        updateCell(navigation.getCellAddress(), c -> c.style(style));
        return this;
    }

    @Override
    public Template applyStyle(Style style, String address) {
        updateCell(new CellAddress(address), c -> c.style(style));
        return this;
    }

    @Override
    public Template applyStyle(Style style, String fromAddress, String toAddress) {
        CellAddress from = new CellAddress(fromAddress);
        CellAddress to = new CellAddress(toAddress);
        Function<WriterCell, WriterCell> builder = c -> c.style(style);
        for (int rowNo = from.getRow(); rowNo <= to.getRow(); rowNo++) {
            for (int colNo = from.getColumn(); colNo <= to.getColumn(); colNo++) {
                updateCell(new CellAddress(rowNo, colNo), builder);
            }
        }
        return this;
    }

    @Override
    public Iterator<WriterCell> iterator() {
        return cells.values().iterator();
    }

    public ByteArrayInputStream getFile() {
        try (Editor editor = new Editor()) {
            editor.goToSheet(0);
            editor.writeTemplate(this);
            return editor.exportToFile();
        }
    }
}
