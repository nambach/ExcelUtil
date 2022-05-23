package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.constraint.Constraint;
import io.github.nambach.excelutil.style.HSSFColorCache;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.PixelUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static io.github.nambach.excelutil.util.ListUtil.groupBy;

@Log4j2
public class Editor implements BaseEditor, FreestyleWriter<Editor>, AutoCloseable, Iterable<Sheet> {
    private final Workbook workbook;
    private final BaseWriter writer;
    private final BaseReader reader;
    private final PointerNavigation navigation = new PointerNavigation();
    private Sheet currentSheet;
    private Style tempStyle;
    private boolean isDebug;

    public Editor() {
        this(getWorkbookFromStream(null));
    }

    public Editor(InputStream stream) {
        this(getWorkbookFromStream(stream));
    }

    public Editor(Workbook workbook) {
        if (workbook == null) {
            workbook = new XSSFWorkbook();
        }

        this.workbook = workbook;
        this.writer = new BaseWriter(workbook);
        this.reader = new BaseReader();

        // set active sheet as current
        if (workbook.getNumberOfSheets() != 0) {
            int index = workbook.getActiveSheetIndex();
            this.currentSheet = workbook.getSheetAt(index);
        }
    }

    @SneakyThrows
    private static Workbook getWorkbookFromStream(InputStream stream) {
        if (stream != null) {
            return WorkbookFactory.create(stream);
        } else {
            return new XSSFWorkbook();
        }
    }

    /**
     * If there is no current sheet, it will create "Sheet1"
     *
     * @return current sheet
     */
    private Sheet getSheet() {
        if (this.currentSheet == null) {
            goToSheet(0);
        }
        return this.currentSheet;
    }

    @Override
    @SneakyThrows
    public void close() {
        this.workbook.close();
    }

    public Workbook getPoiWorkbook() {
        return workbook;
    }

    public Sheet getCurrentPoiSheet() {
        return this.currentSheet;
    }

    public Cell getCurrentPoiCell() {
        if (this.currentSheet == null) {
            return null;
        }
        return getCellAt(currentSheet, navigation);
    }

    // Sheet navigation
    private int getNextRowIndex() {
        return getSheet().getLastRowNum() + 1;
    }

    /**
     * Navigate to sheet at provided index. If there is no sheet, "Sheet1" will be created
     *
     * @param index target index
     * @return current editor
     */
    public Editor goToSheet(int index) {
        // Empty workbook => create the first sheet
        if (workbook.getNumberOfSheets() == 0) {
            goToSheet("Sheet1");
            return this;
        }

        // Get the existing sheet
        if (index < 0) {
            this.currentSheet = workbook.getSheetAt(0);
        } else if (index + 1 > workbook.getNumberOfSheets()) {
            this.currentSheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);
        } else {
            this.currentSheet = workbook.getSheetAt(index);
        }

        resetPointer();
        return this;
    }

    /**
     * Navigate to sheet with provided name. If no sheet was found, a new sheet will be appended with provided name.
     *
     * @param sheetName name of sheet
     * @return current editor
     */
    public Editor goToSheet(String sheetName) {
        if (workbook.getSheet(sheetName) != null) {
            this.currentSheet = workbook.getSheet(sheetName);
        } else {
            this.currentSheet = workbook.createSheet(sheetName);
        }

        resetPointer();
        return this;
    }

    public String getSheetName() {
        return currentSheet == null ? null : currentSheet.getSheetName();
    }

    public Editor setSheetName(String sheetName) {
        int index = workbook.getSheetIndex(getSheet());
        workbook.setSheetName(index, sheetName);
        return this;
    }

    public int getTotalSheets() {
        return workbook.getNumberOfSheets();
    }

    @Override
    public Iterator<Sheet> iterator() {
        return new Iterator<Sheet>() {
            @Override
            public boolean hasNext() {
                return workbook.iterator().hasNext();
            }

            @Override
            public Sheet next() {
                currentSheet = workbook.iterator().next();
                return currentSheet;
            }
        };
    }

    // Cell navigation
    private void resetPointer() {
        goToCell(0, 0);
    }

    @Override
    public Editor goToCell(String address) {
        navigation.goToCell(address);
        return this;
    }

    @Override
    public Editor goToCell(int row, int col) {
        navigation.goToCell(row, col);
        return this;
    }

    @Override
    public Editor next() {
        navigation.next();
        return this;
    }

    @Override
    public Editor next(int steps) {
        navigation.next(steps);
        return this;
    }

    @Override
    public Editor down() {
        navigation.down();
        return this;
    }

    @Override
    public Editor down(int steps) {
        navigation.down(steps);
        return this;
    }

    @Override
    public Editor enter() {
        navigation.update(getNextRowIndex(), 0);
        return this;
    }

    @Override
    public Editor enter(int steps) {
        if (steps > 0) {
            this.enter();
            navigation.down(steps - 1);
        }
        return this;
    }

    // For writing
    private void replaceStyle(CellAddress cellAddress, Style style) {
        Row row = getRowAt(getSheet(), cellAddress.getRow());
        Cell cell = getCellAt(row, cellAddress.getColumn());
        cell.setCellStyle(writer.cachedStyles.accumulate(style));
    }

    @Override
    public Editor useStyle(Style style) {
        this.tempStyle = style;
        return this;
    }

    @Override
    public Editor applyStyle() {
        if (tempStyle != null) {
            replaceStyle(navigation.getCellAddress(), tempStyle);
        }
        return this;
    }

    @Override
    public Editor applyStyle(Style style, String... address) {
        if (style != null && (address == null || address.length == 0)) {
            replaceStyle(navigation.getCellAddress(), style);
        } else {
            applyStyle(style, Arrays.asList(address));
        }
        return this;
    }

    @Override
    public Editor applyStyle(Style style, Collection<String> addresses) {
        Sheet sheet = getSheet();

        if (style != null) {
            Collection<CellAddress> cellAddresses = parseAddress(addresses);
            Map<Integer, List<CellAddress>> rowMap = groupBy(cellAddresses, CellAddress::getRow);
            rowMap.forEach((rowNum, cols) -> {
                Row row = getRowAt(sheet, rowNum);
                for (CellAddress col : cols) {
                    Cell cell = getCellAt(row, col.getColumn());
                    cell.setCellStyle(writer.cachedStyles.accumulate(style));
                }
            });
        }
        return this;
    }

    @Override
    public Editor applyConstraint(Constraint constraint, String... address) {
        if (address == null || address.length == 0) {
            Cell cell = getCellAt(getSheet(), navigation);
            writer.constraintHandler.applyConstraint(constraint, cell);
        } else {
            applyConstraint(constraint, Arrays.asList(address));
        }
        return this;
    }

    @Override
    public Editor applyConstraint(Constraint constraint, Collection<String> addresses) {
        Collection<CellAddress> cellAddresses = parseAddress(addresses);
        Map<Integer, List<CellAddress>> rowMap = groupBy(cellAddresses, CellAddress::getRow);
        Sheet sheet = getSheet();

        rowMap.forEach((rowNum, cols) -> {
            Row row = getRowAt(sheet, rowNum);
            for (CellAddress col : cols) {
                Cell cell = getCellAt(row, col.getColumn());
                writer.constraintHandler.applyConstraint(constraint, cell);
            }
        });
        return this;
    }

    @Override
    public Editor writeComment(UnaryOperator<WriterComment> builder) {
        Cell cell = getCellAt(getSheet(), navigation);
        writer.writeComment(builder.apply(new WriterComment()), cell);
        return this;
    }

    @Override
    public Editor writeCell(UnaryOperator<WriterCell> f) {
        WriterCell writerCell = f.apply(new WriterCell(navigation.getCellAddress(), tempStyle));
        Cell cell = getCellAt(getSheet(), navigation);
        writer.writeCellInfo(writerCell, cell);

        // update pivot
        navigation.updatePivotRight(writerCell.getColSpan() - 1);
        navigation.updatePivotDown(writerCell.getRowSpan() - 1);
        return this;
    }

    public Editor writeTemplate(Template template) {
        writer.writeTemplate(getSheet(), template);

        int[] rowIndex = template.getRowIndexRange();
        int[] colIndex = template.getColIndexRange();

        // update pointer
        navigation.update(rowIndex[0], colIndex[0]);

        // update pivot
        navigation.updatePivot(rowIndex[1], colIndex[1]);
        return this;
    }

    public <T> Editor writeData(DataTemplate<T> template, Collection<T> data) {
        if (data == null) {
            data = Collections.emptyList();
        }
        writer.writeData(getSheet(), template, data, navigation.getRow(), navigation.getCol());

        // update pivot
        navigation.updatePivotRight(template.size() - 1);
        int headerRows = template.isNoHeader() ? 0 : 1;
        navigation.updatePivotDown(data.size() - 1 + headerRows);

        return this;
    }

    public ByteArrayInputStream exportToFile() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            out.close();
            log.info("Excel file was successfully saved.");
            if (isDebug) {
                log.info(writer.cachedStyles.printTotalStyle());
            }
            return in;
        } catch (Exception e) {
            log.error("There some error writing excel file.", e);
            return null;
        }
    }

    // For reading

    public String readString() {
        return getReaderCellAt(getSheet(), navigation).readString();
    }

    public Date readDate() {
        return getReaderCellAt(getSheet(), navigation).readDate();
    }

    public LocalDateTime readLocalDateTime() {
        return getReaderCellAt(getSheet(), navigation).readLocalDateTime();
    }

    public Double readDouble() {
        return getReaderCellAt(getSheet(), navigation).readDouble();
    }

    public Float readFloat() {
        return getReaderCellAt(getSheet(), navigation).readFloat();
    }

    public Long readLong() {
        return getReaderCellAt(getSheet(), navigation).readLong();
    }

    public Integer readInt() {
        return getReaderCellAt(getSheet(), navigation).readInt();
    }

    public Boolean readBoolean() {
        return getReaderCellAt(getSheet(), navigation).readBoolean();
    }

    public <T> Result<T> readSection(ReaderConfig<T> config) {
        return reader.readSheet(getSheet(), config, navigation.getRow(), navigation.getCol());
    }

    public Editor configWorkbook(UnaryOperator<WorkbookConfig> f) {
        f.apply(new WorkbookConfig(this));
        return this;
    }

    public Editor configSheet(UnaryOperator<SheetConfig> f) {
        f.apply(new SheetConfig(this));
        return this;
    }

    public static class WorkbookConfig {
        private final Editor editor;

        public WorkbookConfig(Editor editor) {
            this.editor = editor;
        }

        public WorkbookConfig setXLSColorPolicy(HSSFColorCache.Policy policy) {
            editor.writer.cachedStyles.setHSSFColorPolicy(policy);
            return this;
        }
    }

    public static class SheetConfig {
        private final Editor editor;

        public SheetConfig(Editor editor) {
            this.editor = editor;
        }

        public SheetConfig freeze(int rows, int cols) {
            if (rows < 0) {
                rows = 0;
            }
            if (cols < 0) {
                cols = 0;
            }
            editor.getSheet().createFreezePane(cols, rows);
            return this;
        }

        public SheetConfig setColumnWidth(int pixels, int... colIndexes) {
            if (colIndexes == null) return this;

            Sheet sheet = editor.getSheet();
            for (int colIndex : colIndexes) {
                if (colIndex >= 0) {
                    PixelUtil.setColumnWidth(sheet, colIndex, pixels);
                }
            }

            return this;
        }


        public SheetConfig autoSizeColumn(int... colIndexes) {
            if (colIndexes == null) return this;

            Sheet sheet = editor.getSheet();
            for (int colIndex : colIndexes) {
                if (colIndex >= 0) {
                    sheet.autoSizeColumn(colIndex);
                }
            }

            return this;
        }

        public SheetConfig setRowHeightInPoints(int height, int... rowIndexes) {
            if (rowIndexes == null) return this;

            Sheet sheet = editor.getSheet();
            for (int rowIndex : rowIndexes) {
                if (rowIndex >= 0) {
                    Row row = editor.getRowAt(sheet, rowIndex);
                    row.setHeightInPoints(height);
                }
            }

            return this;
        }

        /**
         * Solution from https://stackoverflow.com/a/29131284/11869677
         *
         * @param rowIndexes list of rows' index
         * @return editor configuration
         */
        public SheetConfig autoSizeRow(int... rowIndexes) {
            if (rowIndexes == null) return this;

            Sheet sheet = editor.getSheet();
            for (int rowIndex : rowIndexes) {
                if (rowIndex < 0) continue;

                Row row = editor.getRowAt(sheet, rowIndex);
                if (editor.workbook instanceof XSSFWorkbook) {
                    row.setHeight((short) -1);
                } else if (editor.workbook instanceof HSSFWorkbook) {
                    CellStyle style = row.getRowStyle();
                    if (style != null) {
                        style.setWrapText(true);
                    }
                }
            }

            return this;
        }

        public SheetConfig hideGrid(boolean b) {
            editor.getSheet().setDisplayGridlines(!b);
            return this;
        }

        public SheetConfig setZoom(int percentage) {
            editor.getSheet().setZoom(percentage);
            return this;
        }

        public SheetConfig debug(boolean b) {
            editor.isDebug = b;
            return this;
        }
    }
}
