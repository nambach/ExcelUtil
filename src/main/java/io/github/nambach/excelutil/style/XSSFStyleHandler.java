package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.util.Map;

public class XSSFStyleHandler extends StyleHandler {
    private final Map<String, XSSFColor> colorCache;
    private final Style s;

    public XSSFStyleHandler(Style s, Map<String, XSSFColor> colorCache) {
        this.s = s;
        this.colorCache = colorCache;
    }

    @Override
    public CellStyle renderCellStyle(Workbook workbook) {
        return this.process((XSSFWorkbook) workbook);
    }

    public XSSFCellStyle process(XSSFWorkbook workbook) {
        System.out.println("New style created");

        XSSFCellStyle style = workbook.createCellStyle();

        processFont(workbook, style);

        if (s.date == Boolean.TRUE) {
            String pattern = s.datePattern != null ? s.datePattern : "MMM dd, yyyy";
            style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(pattern));
        }

        if (s.backgroundColorInHex != null) {
            style.setFillForegroundColor(parseColorHex(workbook, s.backgroundColorInHex, StyleConstant.WHITE));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        processBorder(workbook, style);

        if (s.horizontalAlignment != null) {
            style.setAlignment(s.horizontalAlignment);
        }
        if (s.verticalAlignment != null) {
            style.setVerticalAlignment(s.verticalAlignment);
        }

        return style;
    }

    private void processFont(XSSFWorkbook workbook, XSSFCellStyle style) {
        XSSFFont font = workbook.createFont();
        if (s.fontName != null) {
            font.setFontName(s.fontName);
        }
        if (s.fontSize != null && s.fontSize != 0) {
            font.setFontHeightInPoints(s.fontSize);
        }
        if (s.bold == Boolean.TRUE) {
            font.setBold(true);
        }
        if (s.underline == Boolean.TRUE) {
            font.setUnderline(FontUnderline.SINGLE);
        }
        if (s.fontColorInHex != null) {
            font.setColor(parseColorHex(workbook, s.fontColorInHex, StyleConstant.BLACK));
        }
        style.setFont(font);
    }

    private void processBorder(XSSFWorkbook workbook, XSSFCellStyle style) {
        if (s.borders == null) {
            return;
        }
        for (Border border : s.borders) {
            if (border.getSide() == null) {
                return;
            }
            XSSFColor color = parseColorHex(workbook, border.getHexColor(), StyleConstant.BLACK);
            BorderStyle borderStyle = border.getBorderStyle();
            switch (border.getSide()) {
                case NONE:
                    style.setBorderTop(BorderStyle.NONE);
                    style.setBorderBottom(BorderStyle.NONE);
                    style.setBorderLeft(BorderStyle.NONE);
                    style.setBorderRight(BorderStyle.NONE);
                    return;
                case LEFT:
                    style.setBorderLeft(borderStyle);
                    style.setLeftBorderColor(color);
                    break;
                case TOP:
                    style.setBorderTop(borderStyle);
                    style.setTopBorderColor(color);
                    break;
                case RIGHT:
                    style.setBorderRight(borderStyle);
                    style.setRightBorderColor(color);
                    break;
                case BOTTOM:
                    style.setBorderBottom(borderStyle);
                    style.setBottomBorderColor(color);
                    break;
                case FULL:
                    style.setBorderLeft(borderStyle);
                    style.setBorderRight(borderStyle);
                    style.setBorderTop(borderStyle);
                    style.setBorderBottom(borderStyle);

                    style.setLeftBorderColor(color);
                    style.setRightBorderColor(color);
                    style.setTopBorderColor(color);
                    style.setBottomBorderColor(color);
                    break;
            }
        }

    }

    /**
     * https://stackoverflow.com/a/7471183
     * https://stackoverflow.com/a/23490977
     */
    private XSSFColor parseColorHex(XSSFWorkbook workbook, String hex, String fallbackHex) {
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
