package io.github.nambach.excelutil.core;


import io.github.nambach.excelutil.util.ListUtil;
import io.github.nambach.excelutil.util.TextUtil;
import io.github.nambach.excelutil.validator.builtin.DecimalValidator;
import io.github.nambach.excelutil.validator.builtin.IntegerValidator;
import io.github.nambach.excelutil.validator.builtin.StringValidator;
import io.github.nambach.excelutil.validator.builtin.TypeValidator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Contains information of the current read cell.
 */
public class ReaderCell extends ReaderController {
    /**
     * Original Apache POI {@link Cell}
     */
    private final Cell cell;
    private final String columnTitle;

    ReaderCell(Cell cell, String columnTitle, ReaderConfig<?> config, Result<?> result) {
        super(config, result);
        this.cell = cell;
        this.columnTitle = columnTitle;
    }

    public static ReaderCell wrap(Cell cell) {
        return new ReaderCell(cell, null, null, null);
    }

    /**
     * @return the Apache POI {@link Cell}.
     */
    public Cell getPoiCell() {
        return cell;
    }

    /**
     * @return the current column title based on the title row you specify
     * through the method {@link ReaderConfig#titleAtRow(int)}.
     */
    public String getColumnTitle() {
        return this.columnTitle;
    }

    /**
     * @return the current column index.
     */
    public int getColumnIndex() {
        return cell.getColumnIndex();
    }

    /**
     * @return the current row index.
     */
    public int getRowIndex() {
        return cell.getRowIndex();
    }

    /**
     * @return the cell address in string (for example: A1, B2...).
     */
    public String getAddress() {
        return cell.getAddress().formatAsString();
    }

    private Double tryParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Boolean tryParseBoolean(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return string value of the cell.
     */
    public String readString() {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    /**
     * @return date value of the cell as {@link Date} (if the actually data is stored as date).
     */
    public Date readDate() {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue();
        }
        return null;
    }

    /**
     * @return date value of the cell as {@link LocalDateTime} (if the actually data is stored as date).
     */
    public LocalDateTime readLocalDateTime() {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue();
        }
        return null;
    }

    /**
     * @return the double value of cell (if cell is numeric).
     */
    public Double readDouble() {
        switch (cell.getCellType()) {
            case STRING:
                return tryParseDouble(cell.getStringCellValue());
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return (double) (cell.getBooleanCellValue() ? 1 : 0);
            default:
                return null;
        }
    }

    /**
     * @return the float value of cell (if cell is numeric).
     */
    public Float readFloat() {
        Double d = readDouble();
        return d == null ? null : d.floatValue();
    }

    /**
     * @return the long value of cell (if cell is numeric).
     */
    public Long readLong() {
        Double d = readDouble();
        return d == null ? null : d.longValue();
    }

    /**
     * @return the int value of cell (if cell is numeric).
     */
    public Integer readInt() {
        Double d = readDouble();
        return d == null ? null : d.intValue();
    }

    /**
     * @return the boolean value of cell (if cell is boolean type).
     */
    public Boolean readBoolean() {
        switch (cell.getCellType()) {
            case STRING:
                return tryParseBoolean(cell.getStringCellValue());
            case NUMERIC:
                return cell.getNumericCellValue() != 0;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return null;
        }
    }

    @Override
    public void setError(String message) {
        result.newRowError(getRowIndex()).setCustomError(message);
    }

    @Override
    public void throwError(String message) {
        this.setError(message);
        super.terminateNow();
    }

    void validate(TypeValidator typeValidator, String fieldName) {
        List<String> errors = null;
        if (typeValidator instanceof StringValidator) {
            String val = readString();
            errors = typeValidator.test(val);
        } else if (typeValidator instanceof DecimalValidator) {
            try {
                Double val = readDoubleRisky();
                errors = typeValidator.test(val);
            } catch (NumberFormatException e) {
                errors = Collections.singletonList("need to be a decimal");
            }
        } else if (typeValidator instanceof IntegerValidator) {
            try {
                Long val = readLongRisky();
                errors = typeValidator.test(val);
            } catch (NumberFormatException e) {
                errors = Collections.singletonList("need to be an integer");
            }
        }

        // set errors to result
        if (ListUtil.hasMember(errors)) {
            String field = TextUtil.getNotNull(fieldName, columnTitle, "Column " + (getColumnIndex() + 1));
            result.newRowError(getRowIndex()).appendError(field, errors);
            if (isEarlyExit()) {
                super.terminateNow();
            }
        }
    }

    private Double readDoubleRisky() throws NumberFormatException {
        switch (cell.getCellType()) {
            case STRING:
                String strVal = cell.getStringCellValue();
                if (strVal == null) {
                    return null;
                }
                return Double.parseDouble(strVal);
            case NUMERIC:
                return cell.getNumericCellValue();
            default:
                throw new NumberFormatException();
        }
    }

    private Long readLongRisky() throws NumberFormatException {
        switch (cell.getCellType()) {
            case STRING:
                String strVal = cell.getStringCellValue();
                if (strVal == null) {
                    return null;
                }
                return Long.parseLong(strVal);
            case NUMERIC:
                double val = cell.getNumericCellValue();
                if ((val % 1) == 0) { // test if this is a long
                    return (long) val;
                } else {
                    throw new NumberFormatException();
                }
            default:
                throw new NumberFormatException();
        }
    }
}
