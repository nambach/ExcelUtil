package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public abstract class StyleHandler {

    public abstract CellStyle renderCellStyle(Workbook workbook);

}
