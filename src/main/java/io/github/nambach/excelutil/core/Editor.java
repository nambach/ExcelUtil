package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.HSSFColorCache;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.PixelUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import java.util.function.Function;

import static io.github.nambach.excelutil.util.ListUtil.groupBy;

public class Editor implements BaseEditor, FreestyleWriter<Editor>, AutoCloseable, Iterable<Sheet> {
    private final Workbook workbook;
    private final BaseWriter writer;
    private final BaseReader reader;
    private final PointerNavigation navigation = new PointerNavigation();
    private Sheet currentSheet;
    private Style tempStyle;
    private boolean isDebug;

    public Editor() {
        this(null);
    }

    public Editor(InputStream stream) {
        this(stream, Mode.DEFAULT);
    }

    @SneakyThrows
    public Editor(InputStream stream, Mode mode) {
        switch (mode) {
            case XLS:
                if (stream != null) {
                    workbook = new HSSFWorkbook(stream);
                } else {
                    workbook = new HSSFWorkbook();
                }
                break;
            case XLSX:
                if (stream != null) {
                    workbook = new XSSFWorkbook(stream);
                } else {
                    workbook = new XSSFWorkbook();
                }
                break;
            case LARGE_XLSX:
                if (stream != null) {
                    workbook = new SXSSFWorkbook(new XSSFWorkbook(stream), mode.rowsInMemory);
                } else {
                    workbook = new SXSSFWorkbook(mode.rowsInMemory);
                }
                break;
            default:
                if (stream != null) {
                    workbook = WorkbookFactory.create(stream);
                } else {
                    workbook = new XSSFWorkbook();
                }
                break;
        }

        this.writer = new BaseWriter(workbook, mode);
        this.reader = new BaseReader();

        // set active sheet as current
        if (stream != null && workbook.getNumberOfSheets() != 0) {
            int index = workbook.getActiveSheetIndex();
            currentSheet = workbook.getSheetAt(index);
        }

        // set default sheet
        if (stream == null) {
            goToSheet(0);
        }
    }

    public static Editor openMode(Mode mode) {
        return new Editor(null, mode);
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
        return currentSheet;
    }

    // Sheet navigation
    private void resetPointer() {
        enter();
    }

    private int getNextRowIndex() {
        return currentSheet.getLastRowNum() + 1;
    }

    public Editor goToSheet(int index) {
        if (workbook.getNumberOfSheets() == 0) {
            goToSheet("Sheet 1");
        } else if (index < 0) {
            currentSheet = workbook.getSheetAt(0);
        } else if (index + 1 > workbook.getNumberOfSheets()) {
            currentSheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);
        } else {
            currentSheet = workbook.getSheetAt(index);
        }
        resetPointer();
        return this;
    }

    public Editor goToSheet(String sheetName) {
        if (workbook.getSheet(sheetName) != null) {
            currentSheet = workbook.getSheet(sheetName);
        } else {
            currentSheet = workbook.createSheet(sheetName);
        }
        resetPointer();
        return this;
    }

    public String getSheetName() {
        return currentSheet == null ? null : currentSheet.getSheetName();
    }

    public Editor setSheetName(String sheetName) {
        if (currentSheet != null) {
            workbook.setSheetName(workbook.getSheetIndex(currentSheet), sheetName);
        }
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
        Row row = getRowAt(currentSheet, cellAddress.getRow());
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
        if (style != null) {
            Collection<CellAddress> cellAddresses = parseAddress(addresses);
            Map<Integer, List<CellAddress>> rowMap = groupBy(cellAddresses, CellAddress::getRow);
            rowMap.forEach((rowNum, cols) -> {
                Row row = getRowAt(currentSheet, rowNum);
                for (CellAddress col : cols) {
                    Cell cell = getCellAt(row, col.getColumn());
                    cell.setCellStyle(writer.cachedStyles.accumulate(style));
                }
            });
        }
        return this;
    }

    @Override
    public Editor writeCell(Function<WriterCell, WriterCell> f) {
        WriterCell writerCell = f.apply(new WriterCell(navigation.getCellAddress(), tempStyle));
        Cell cell = getCellAt(currentSheet, navigation);
        writer.writeCellInfo(writerCell, cell);

        // update pivot
        navigation.updatePivotRight(writerCell.getColSpan() - 1);
        navigation.updatePivotDown(writerCell.getRowSpan() - 1);
        return this;
    }

    public Editor writeTemplate(Template template) {
        writer.writeTemplate(currentSheet, template);

        int[] rowIndex = template.getRowIndex();
        int[] colIndex = template.getColIndex();

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
        writer.writeData(currentSheet, template, data, navigation.getRow(), navigation.getCol());

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
            System.out.println("Excel file was successfully saved\n" +
                               (isDebug ? writer.cachedStyles.printTotalStyle() : ""));
            return in;
        } catch (Exception e) {
            System.err.println("There some error writing excel file:");
            e.printStackTrace();
            return null;
        }
    }

    // For reading

    public String readString() {
        return getReaderCellAt(currentSheet, navigation).readString();
    }

    public Date readDate() {
        return getReaderCellAt(currentSheet, navigation).readDate();
    }

    public LocalDateTime readLocalDateTime() {
        return getReaderCellAt(currentSheet, navigation).readLocalDateTime();
    }

    public Double readDouble() {
        return getReaderCellAt(currentSheet, navigation).readDouble();
    }

    public Float readFloat() {
        return getReaderCellAt(currentSheet, navigation).readFloat();
    }

    public Long readLong() {
        return getReaderCellAt(currentSheet, navigation).readLong();
    }

    public Integer readInt() {
        return getReaderCellAt(currentSheet, navigation).readInt();
    }

    public Boolean readBoolean() {
        return getReaderCellAt(currentSheet, navigation).readBoolean();
    }

    public <T> Result<T> readSection(ReaderConfig<T> config) {
        return reader.readSheet(currentSheet, config, navigation.getRow(), navigation.getCol());
    }

    public Editor configSheet(Function<Config, Config> f) {
        f.apply(new Config(this));
        return this;
    }

    public enum Mode {
        DEFAULT,
        XLS,
        XLSX,
        LARGE_XLSX;

        private int rowsInMemory = 100;
        @Getter
        private HSSFColorCache.Policy xlsColorPolicy = HSSFColorCache.Policy.USE_MOST_SIMILAR;

        Mode() {
        }

        public Mode withRowsInMemory(int n) {
            this.rowsInMemory = n;
            return this;
        }

        public Mode withXlsColorPolicy(HSSFColorCache.Policy policy) {
            this.xlsColorPolicy = policy;
            return this;
        }
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
            Sheet sheet = editor.currentSheet;
            sheet.createFreezePane(cols, rows);
            return this;
        }

        public Config setColumnWidth(int pixels, int... colIndexes) {
            if (colIndexes != null && editor.currentSheet != null) {
                for (int colIndex : colIndexes) {
                    if (colIndex >= 0) {
                        PixelUtil.setColumnWidth(editor.currentSheet, colIndex, pixels);
                    }
                }
            }
            return this;
        }

        public Config autoSizeColumn(int... colIndexes) {
            if (colIndexes != null && editor.currentSheet != null) {
                for (int colIndex : colIndexes) {
                    if (colIndex >= 0) {
                        editor.currentSheet.autoSizeColumn(colIndex);
                    }
                }
            }
            return this;
        }

        public Config setRowHeightInPoints(int height, int... rowIndexes) {
            if (rowIndexes != null && editor.currentSheet != null) {
                for (int rowIndex : rowIndexes) {
                    if (rowIndex >= 0) {
                        Row row = editor.getRowAt(editor.currentSheet, rowIndex);
                        row.setHeightInPoints(height);
                    }
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
        public Config autoSizeRow(int... rowIndexes) {
            if (rowIndexes != null && editor.currentSheet != null) {
                for (int rowIndex : rowIndexes) {
                    if (rowIndex >= 0) {
                        Row row = editor.getRowAt(editor.currentSheet, rowIndex);
                        if (editor.workbook instanceof XSSFWorkbook) {
                            row.setHeight((short) -1);
                        } else if (editor.workbook instanceof HSSFWorkbook) {
                            CellStyle style = row.getRowStyle();
                            if (style != null) {
                                style.setWrapText(true);
                            }
                        }
                    }
                }
            }
            return this;
        }

        public Config hideGrid(boolean b) {
            if (editor.currentSheet != null) {
                editor.currentSheet.setDisplayGridlines(!b);
            }
            return this;
        }

        public Config setZoom(int percentage) {
            if (editor.currentSheet != null) {
                editor.currentSheet.setZoom(percentage);
            }
            return this;
        }

        public Config debug(boolean b) {
            editor.isDebug = b;
            return this;
        }
    }
}
