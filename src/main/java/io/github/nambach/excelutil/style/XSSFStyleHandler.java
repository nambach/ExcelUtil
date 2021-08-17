package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;

import static io.github.nambach.excelutil.style.StyleProperty.Alignments;
import static io.github.nambach.excelutil.style.StyleProperty.BackgroundColor;
import static io.github.nambach.excelutil.style.StyleProperty.Bold;
import static io.github.nambach.excelutil.style.StyleProperty.DatePattern;
import static io.github.nambach.excelutil.style.StyleProperty.FontColor;
import static io.github.nambach.excelutil.style.StyleProperty.FontName;
import static io.github.nambach.excelutil.style.StyleProperty.FontSize;
import static io.github.nambach.excelutil.style.StyleProperty.Indentation;
import static io.github.nambach.excelutil.style.StyleProperty.Underline;
import static io.github.nambach.excelutil.style.StyleProperty.WrapText;

public class XSSFStyleHandler implements StyleHandler {
    private final XSSFColorCache colorCache; // key as RGB value
    private final XSSFWorkbook workbook;

    public XSSFStyleHandler(XSSFWorkbook workbook, XSSFColorCache colorCache) {
        this.workbook = workbook;
        this.colorCache = colorCache;
    }

    @Override
    public void setBorderTop(Border border, CellStyle cellStyle) {
        cellStyle.setBorderTop(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPredefined()) {
            cellStyle.setTopBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setTopBorderColor(colorCache.getColor(color));
        }
    }

    @Override
    public void setBorderBottom(Border border, CellStyle cellStyle) {
        cellStyle.setBorderBottom(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPredefined()) {
            cellStyle.setBottomBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setBottomBorderColor(colorCache.getColor(color));
        }
    }

    @Override
    public void setBorderLeft(Border border, CellStyle cellStyle) {
        cellStyle.setBorderLeft(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPredefined()) {
            cellStyle.setLeftBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setLeftBorderColor(colorCache.getColor(color));
        }
    }

    @Override
    public void setBorderRight(Border border, CellStyle cellStyle) {
        cellStyle.setBorderRight(border.getBorderStyle());

        StyleColor color = border.getColor();
        if (color.isPredefined()) {
            cellStyle.setRightBorderColor(color.toIndexedColor().index);
        } else {
            ((XSSFCellStyle) cellStyle).setRightBorderColor(colorCache.getColor(color));
        }
    }

    @Override
    public CellStyle renderCellStyle(Style style) {
        if (style == null) {
            return null;
        }
//        System.out.println("New style created");

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        XSSFDataFormat format = creationHelper.createDataFormat();

        processFont(style, cellStyle);

        style.getProperty(Indentation).getShort().ifPresent(cellStyle::setIndention);
        style.getProperty(WrapText).getBoolean().ifPresent(cellStyle::setWrapText);
        style.getProperty(DatePattern).getString().ifPresent(pattern -> cellStyle.setDataFormat(format.getFormat(pattern)));

        style.getProperty(BackgroundColor).getAny(StyleColor.class).ifPresent(color -> {
            if (color.isPredefined()) {
                cellStyle.setFillForegroundColor(color.toIndexedColor().index);
            } else {
                cellStyle.setFillForegroundColor(colorCache.getColor(color));
            }
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        });

        style.getProperty(Alignments).getAny(ArrayList.class).ifPresent(list -> {
            for (Object o : list) {
                if (o instanceof VerticalAlignment) {
                    cellStyle.setVerticalAlignment((VerticalAlignment) o);
                } else if (o instanceof HorizontalAlignment) {
                    cellStyle.setAlignment((HorizontalAlignment) o);
                }
            }
        });

        processBorder(style, cellStyle);

        return cellStyle;
    }

    @Override
    public void processFont(Style style, CellStyle cellStyle) {
        XSSFFont font = workbook.createFont();

        style.getProperty(FontName).getString().ifPresent(font::setFontName);
        style.getProperty(FontSize).getShort().ifPresent(font::setFontHeightInPoints);
        style.getProperty(Bold).getBoolean().ifPresent(font::setBold);
        style.getProperty(Underline).getBoolean().ifPresent(bool -> font.setUnderline(FontUnderline.SINGLE));
        style.getProperty(FontColor).getAny(StyleColor.class).ifPresent(color -> {
            if (color.isPredefined()) {
                font.setColor(color.toIndexedColor().index);
            } else {
                font.setColor(colorCache.getColor(color));
            }
        });

        cellStyle.setFont(font);
    }

    public int countColors() {
        return colorCache.size();
    }
}
