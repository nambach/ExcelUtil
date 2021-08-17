package io.github.nambach.excelutil.style;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import static io.github.nambach.excelutil.style.StyleProperty.BackgroundColor;
import static io.github.nambach.excelutil.style.StyleProperty.FontColor;

public class HSSFStyleHandler extends StyleHandler {

    private final HSSFWorkbook workbook;
    private final HSSFColorCache colorCache;

    public HSSFStyleHandler(HSSFWorkbook workbook, HSSFColorCache colorCache) {
        this.workbook = workbook;
        this.colorCache = colorCache;
    }

    @Override
    Workbook getWorkbook() {
        return workbook;
    }

    @Override
    public CellStyle renderCellStyle(Style style) {
        if (style == null) {
            return null;
        }

        HSSFCellStyle cellStyle = (HSSFCellStyle) super.renderCellStyle(style);

        style.getProperty(BackgroundColor).getAny(StyleColor.class).ifPresent(color -> {
            cellStyle.setFillForegroundColor(colorCache.getIndex(color));
        });

        return cellStyle;
    }

    @Override
    protected Font renderFont(Style style) {
        HSSFFont font = (HSSFFont) super.renderFont(style);

        style.getProperty(FontColor).getAny(StyleColor.class).ifPresent(color -> {
            font.setColor(colorCache.getIndex(color));
        });

        return font;
    }

    @Override
    void setBorderTop(Border border, CellStyle cellStyle) {
        cellStyle.setBorderTop(border.getBorderStyle());
        cellStyle.setTopBorderColor(colorCache.getIndex(border.getColor()));
    }

    @Override
    void setBorderBottom(Border border, CellStyle cellStyle) {
        cellStyle.setBorderBottom(border.getBorderStyle());
        cellStyle.setBottomBorderColor(colorCache.getIndex(border.getColor()));
    }

    @Override
    void setBorderLeft(Border border, CellStyle cellStyle) {
        cellStyle.setBorderLeft(border.getBorderStyle());
        cellStyle.setLeftBorderColor(colorCache.getIndex(border.getColor()));
    }

    @Override
    void setBorderRight(Border border, CellStyle cellStyle) {
        cellStyle.setBorderRight(border.getBorderStyle());
        cellStyle.setRightBorderColor(colorCache.getIndex(border.getColor()));
    }

    @Override
    public int countColors() {
        return colorCache.size();
    }
}
