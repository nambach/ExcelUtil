package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.constraint.Constraint;
import io.github.nambach.excelutil.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.util.CellAddress;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class Template implements Iterable<WriterCell>, FreestyleWriter<Template> {
    private final Map<String, WriterCell> cells = new HashMap<>();
    private final PointerNavigation navigation = new PointerNavigation();
    private Style tempStyle;

    public Template() {
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

    /**
     * Update cell at particular address
     *
     * @param cellAddress address of cell
     * @param builder     transform function
     * @return current template
     */
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
        updateCell(navigation.getCellAddress(), c -> c.replaceStyle(tempStyle));
        return this;
    }

    @Override
    public Template applyStyle(Style style, String... address) {
        if (address == null || address.length == 0) {
            updateCell(navigation.getCellAddress(), c -> c.replaceStyle(style));
        } else {
            applyStyle(style, Arrays.asList(address));
        }
        return this;
    }

    @Override
    public Template applyStyle(Style style, Collection<String> addresses) {
        Function<WriterCell, WriterCell> builder = c -> c.replaceStyle(style);

        Collection<CellAddress> cellAddresses = parseAddress(addresses);
        for (CellAddress cellAddress : cellAddresses) {
            updateCell(cellAddress, builder);
        }
        return this;
    }

    @Override
    public Template applyConstraint(Constraint constraint, String... address) {
        if (address == null || address.length == 0) {
            updateCell(navigation.getCellAddress(), c -> c.constraint(constraint));
        } else {
            applyConstraint(constraint, Arrays.asList(address));
        }
        return this;
    }

    @Override
    public Template applyConstraint(Constraint constraint, Collection<String> addresses) {
        Function<WriterCell, WriterCell> builder = c -> c.constraint(constraint);

        Collection<CellAddress> cellAddresses = parseAddress(addresses);
        for (CellAddress cellAddress : cellAddresses) {
            updateCell(cellAddress, builder);
        }
        return this;
    }

    @Override
    public Template writeComment(Function<WriterComment, WriterComment> builder) {
        updateCell(navigation.getCellAddress(), c -> c.comment(builder));
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
