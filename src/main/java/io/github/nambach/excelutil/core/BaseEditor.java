package io.github.nambach.excelutil.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

interface BaseEditor {
    default Row getRowAt(Sheet sheet, int rowAt) {
        Row row = sheet.getRow(rowAt);
        if (row == null) {
            row = sheet.createRow(rowAt);
        }
        return row;
    }

    default Cell getCellAt(Row row, int colAt) {
        return row.getCell(colAt, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    default Cell getCellAt(Sheet sheet, PointerNavigation navigation) {
        Row row = getRowAt(sheet, navigation.getRow());
        return getCellAt(row, navigation.getCol());
    }

    default ReaderCell getReaderCellAt(Sheet sheet, PointerNavigation pointer) {
        Cell cell = getCellAt(getRowAt(sheet, pointer.getRow()), pointer.getCol());
        return ReaderCell.wrap(cell);
    }

    default boolean isDateType(Object value) {
        return value instanceof Date ||
               value instanceof LocalDateTime ||
               value instanceof LocalDate;
    }
}
