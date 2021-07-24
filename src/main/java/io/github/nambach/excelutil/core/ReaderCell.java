package io.github.nambach.excelutil.core;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Contains information of the current read cell.
 */
public class ReaderCell {
    /**
     * Original Apache POI {@link Cell}
     */
    private final Cell cell;
    private final String columnTitle;

    ReaderCell(Cell cell, String columnTitle) {
        this.cell = cell;
        this.columnTitle = columnTitle;
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
}
