package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static io.github.nambach.excelutil.style.StyleProperty.BackgroundColor;
import static io.github.nambach.excelutil.style.StyleProperty.FontColor;

public class XSSFStyleHandler extends StyleHandler {

    private final XSSFWorkbook workbook;
    private final XSSFColorCache colorCache;

    public XSSFStyleHandler(XSSFWorkbook workbook, XSSFColorCache colorCache) {
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

        XSSFCellStyle cellStyle = (XSSFCellStyle) super.renderCellStyle(style);

        style.getProperty(BackgroundColor).getAny(StyleColor.class).ifPresent(color -> {
            if (color.isCustom()) {
                cellStyle.setFillForegroundColor(colorCache.getColor(color));
            }
        });

        return cellStyle;
    }

    @Override
    protected Font renderFont(Style style) {
        XSSFFont font = (XSSFFont) super.renderFont(style);

        style.getProperty(FontColor).getAny(StyleColor.class).ifPresent(color -> {
            if (color.isCustom()) {
                font.setColor(colorCache.getColor(color));
            }
        });

        return font;
    }

    @Override
    public void setBorderTop(Border border, CellStyle cellStyle) {
        cellStyle.setBorderTop(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPreset()) {
            cellStyle.setTopBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setTopBorderColor(colorCache.getColor(color));
        }
    }

    @Override
    public void setBorderBottom(Border border, CellStyle cellStyle) {
        cellStyle.setBorderBottom(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPreset()) {
            cellStyle.setBottomBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setBottomBorderColor(colorCache.getColor(color));
        }
    }

    @Override
    public void setBorderLeft(Border border, CellStyle cellStyle) {
        cellStyle.setBorderLeft(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPreset()) {
            cellStyle.setLeftBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setLeftBorderColor(colorCache.getColor(color));
        }
    }

    @Override
    public void setBorderRight(Border border, CellStyle cellStyle) {
        cellStyle.setBorderRight(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPreset()) {
            cellStyle.setRightBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setRightBorderColor(colorCache.getColor(color));
        }
    }

    public int countColors() {
        return colorCache.size();
    }
}
