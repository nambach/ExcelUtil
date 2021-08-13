package io.github.nambach.excelutil.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

interface BaseEditor {
    default Row getRowAt(Sheet sheet, int rowAt) {
        Row row = sheet.getRow(rowAt);
        if (row == null) {
            row = sheet.createRow(rowAt);
        }
        return row;
    }

    default Cell getCellAt(Row row, int colAt) {
        Cell cell = row.getCell(colAt);
        if (cell == null) {
            cell = row.createCell(colAt);
        }
        return cell;
    }

    default ReaderCell getCellAt(Sheet sheet, PointerNavigation pointer) {
        Cell cell = getCellAt(getRowAt(sheet, pointer.getRow()), pointer.getCol());
        return ReaderCell.wrap(cell);
    }
}
