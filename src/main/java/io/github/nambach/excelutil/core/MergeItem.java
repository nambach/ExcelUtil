package io.nambm.excel.core;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

@Getter
@Setter
class MergeItem {
    private Object lastValue;
    private int fromRow;
    private int toRow;

    public MergeItem(Object lastValue, int fromRow, int toRow) {
        this.lastValue = lastValue;
        this.fromRow = fromRow;
        this.toRow = toRow;
    }

    public void handleMerge(Sheet sheet, Cell cell) {
        if (this.needMerge()) {
            cell.getCellStyle().setWrapText(true);
            sheet.addMergedRegion(new CellRangeAddress(this.fromRow, this.toRow,
                                                       cell.getColumnIndex(), cell.getColumnIndex()));
        }
    }

    public void reset(Object currentValue, int startRow) {
        fromRow = startRow;
        toRow = startRow;
        lastValue = currentValue;
    }

    public void increaseRange() {
        toRow++;
    }

    public boolean needMerge() {
        return toRow - fromRow > 0;
    }
}
