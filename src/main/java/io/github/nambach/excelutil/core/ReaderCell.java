package io.github.nambach.excelutil.core;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.time.LocalDateTime;
import java.util.Date;

public class ReaderCell {
    private final Cell cell;
    private final String columnTitle;

    public ReaderCell(Cell cell, String columnTitle) {
        this.cell = cell;
        this.columnTitle = columnTitle;
    }

    public String getColumnTitle() {
        return this.columnTitle;
    }

    public int getColumnIndex() {
        return cell.getColumnIndex();
    }

    public int getRowIndex() {
        return cell.getRowIndex();
    }

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

    public Date readDate() {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue();
        }
        return null;
    }

    public LocalDateTime readLocalDateTime() {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue();
        }
        return null;
    }

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

    public Float readFloat() {
        Double d = readDouble();
        return d == null ? null : d.floatValue();
    }

    public Long readLong() {
        Double d = readDouble();
        return d == null ? null : d.longValue();
    }

    public Integer readInt() {
        Double d = readDouble();
        return d == null ? null : d.intValue();
    }

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
