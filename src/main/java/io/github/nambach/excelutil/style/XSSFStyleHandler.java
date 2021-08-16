package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import static io.github.nambach.excelutil.style.StyleProperty.Alignments;
import static io.github.nambach.excelutil.style.StyleProperty.BackgroundColorInHex;
import static io.github.nambach.excelutil.style.StyleProperty.Bold;
import static io.github.nambach.excelutil.style.StyleProperty.DatePattern;
import static io.github.nambach.excelutil.style.StyleProperty.FontColorInHex;
import static io.github.nambach.excelutil.style.StyleProperty.FontName;
import static io.github.nambach.excelutil.style.StyleProperty.FontSize;
import static io.github.nambach.excelutil.style.StyleProperty.Indentation;
import static io.github.nambach.excelutil.style.StyleProperty.Underline;
import static io.github.nambach.excelutil.style.StyleProperty.WrapText;

public class XSSFStyleHandler implements StyleHandler {
    private final Map<String, XSSFColor> colorCache;
    private final XSSFWorkbook workbook;
    private final Style style;

    public XSSFStyleHandler(Style style, XSSFWorkbook workbook, Map<String, XSSFColor> colorCache) {
        this.style = style;
        this.workbook = workbook;
        this.colorCache = colorCache;
    }


    @Override
    public void setBorderTop(Border border, CellStyle cellStyle) {
        cellStyle.setBorderTop(border.getBorderStyle());

        if (cellStyle instanceof XSSFCellStyle) {
            XSSFColor color = parseColorHex(border.getHexColor(), StyleConstant.BLACK);
            ((XSSFCellStyle) cellStyle).setTopBorderColor(color);
        }
    }

    @Override
    public void setBorderBottom(Border border, CellStyle cellStyle) {
        cellStyle.setBorderBottom(border.getBorderStyle());

        if (cellStyle instanceof XSSFCellStyle) {
            XSSFColor color = parseColorHex(border.getHexColor(), StyleConstant.BLACK);
            ((XSSFCellStyle) cellStyle).setBottomBorderColor(color);
        }
    }

    @Override
    public void setBorderLeft(Border border, CellStyle cellStyle) {
        cellStyle.setBorderLeft(border.getBorderStyle());

        if (cellStyle instanceof XSSFCellStyle) {
            XSSFColor color = parseColorHex(border.getHexColor(), StyleConstant.BLACK);
            ((XSSFCellStyle) cellStyle).setLeftBorderColor(color);
        }
    }

    @Override
    public void setBorderRight(Border border, CellStyle cellStyle) {
        cellStyle.setBorderRight(border.getBorderStyle());

        if (cellStyle instanceof XSSFCellStyle) {
            XSSFColor color = parseColorHex(border.getHexColor(), StyleConstant.BLACK);
            ((XSSFCellStyle) cellStyle).setRightBorderColor(color);
        }
    }

    @Override
    public CellStyle renderCellStyle() {
//        System.out.println("New style created");

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        XSSFDataFormat format = creationHelper.createDataFormat();

        processFont(cellStyle);

        style.getProperty(Indentation).getShort().ifPresent(cellStyle::setIndention);
        style.getProperty(WrapText).getBoolean().ifPresent(cellStyle::setWrapText);
        style.getProperty(DatePattern).getString().ifPresent(pattern -> cellStyle.setDataFormat(format.getFormat(pattern)));

        style.getProperty(BackgroundColorInHex).getString().ifPresent(val -> {
            cellStyle.setFillForegroundColor(parseColorHex(val, StyleConstant.WHITE));
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

    private void processFont(XSSFCellStyle cellStyle) {
        XSSFFont font = workbook.createFont();

        style.getProperty(FontName).getString().ifPresent(font::setFontName);
        style.getProperty(FontSize).getShort().ifPresent(font::setFontHeightInPoints);
        style.getProperty(Bold).getBoolean().ifPresent(font::setBold);
        style.getProperty(Underline).getBoolean().ifPresent(bool -> font.setUnderline(FontUnderline.SINGLE));
        style.getProperty(FontColorInHex).getString().ifPresent(val -> font
                .setColor(parseColorHex(val, StyleConstant.BLACK)));

        cellStyle.setFont(font);
    }

    /**
     * https://stackoverflow.com/a/7471183
     * https://stackoverflow.com/a/23490977
     */
    private XSSFColor parseColorHex(String hex, String fallbackHex) {
        String key = hex;
        if (colorCache.containsKey(key)) {
            return colorCache.get(key);
        }

        Color color;
        try {
            color = Color.decode(hex);
        } catch (Exception e) {
            key = fallbackHex;
            if (colorCache.containsKey(key)) {
                return colorCache.get(key);
            }

            color = Color.decode(fallbackHex);
        }

        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
        XSSFColor rs = new XSSFColor(color, colorMap);

        // save cache
        colorCache.put(key, rs);
        return rs;
    }
}
