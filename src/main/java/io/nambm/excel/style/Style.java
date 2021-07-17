package io.nambm.excel.style;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.UUID;

public class Style {

    @Getter(AccessLevel.PUBLIC)
    private final String uuid = UUID.randomUUID().toString();

    // Text
    String fontName;
    Short fontSize;
    Boolean bold;
    Boolean underline;

    // Date
    Boolean date;
    String datePattern;

    // Color
    String fontColorInHex;
    String backgroundColorInHex;

    // Alignment
    HorizontalAlignment horizontalAlignment;
    VerticalAlignment verticalAlignment;

    // Borders
    LinkedHashSet<Border> borders;

    public Style() {
    }

    public static StyleBuilder builder() {
        return new StyleBuilder();
    }

    public XSSFCellStyle process(CacheStyle cacheStyle) {
        // fetch from cache
        if (cacheStyle.containStyle(uuid)) {
            return cacheStyle.get(uuid);
        }

        // cache not found => create new style
        XSSFCellStyle style = processWithoutCache(cacheStyle.getWorkbook());

        // Save cache
        cacheStyle.put(uuid, style);
        return style;
    }

    public XSSFCellStyle processWithoutCache(XSSFWorkbook workbook) {
        System.out.println("New style created");

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(prepareFont(workbook));

        if (date == Boolean.TRUE) {
            String pattern = datePattern != null ? datePattern : "MMM dd, yyyy";
            style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(pattern));
        }

        if (backgroundColorInHex != null) {
            style.setFillForegroundColor(parseColorHex(workbook, backgroundColorInHex, DefaultStyle.WHITE));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        processBorder(style);

        if (horizontalAlignment != null) {
            style.setAlignment(horizontalAlignment);
        }
        if (verticalAlignment != null) {
            style.setVerticalAlignment(verticalAlignment);
        }

        return style;
    }

    private Font prepareFont(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        if (fontName != null) {
            font.setFontName(fontName);
        }
        if (fontSize != null && fontSize != 0) {
            font.setFontHeightInPoints(fontSize);
        }
        if (bold == Boolean.TRUE) {
            font.setBold(true);
        }
        if (underline == Boolean.TRUE) {
            font.setUnderline(FontUnderline.SINGLE);
        }
        if (fontColorInHex != null) {
            font.setColor(parseColorHex(workbook, fontColorInHex, DefaultStyle.BLACK));
        }
        return font;
    }

    private void processBorder(XSSFCellStyle style) {
        if (borders == null) {
            return;
        }
        for (Border border : borders) {
            if (border.getBorderSide() == null) {
                return;
            }
            switch (border.getBorderSide()) {
                case NONE:
                    style.setBorderTop(BorderStyle.NONE);
                    style.setBorderBottom(BorderStyle.NONE);
                    style.setBorderLeft(BorderStyle.NONE);
                    style.setBorderRight(BorderStyle.NONE);
                    return;
                case LEFT:
                    style.setBorderLeft(border.getBorderStyle());
                    style.setLeftBorderColor(border.getBorderColor());
                    break;
                case TOP:
                    style.setBorderTop(border.getBorderStyle());
                    style.setTopBorderColor(border.getBorderColor());
                    break;
                case RIGHT:
                    style.setBorderRight(border.getBorderStyle());
                    style.setRightBorderColor(border.getBorderColor());
                    break;
                case BOTTOM:
                    style.setBorderBottom(border.getBorderStyle());
                    style.setBottomBorderColor(border.getBorderColor());
                    break;
                case VERTICAL:
                    style.setBorderLeft(border.getBorderStyle());
                    style.setBorderRight(border.getBorderStyle());

                    style.setLeftBorderColor(border.getBorderColor());
                    style.setRightBorderColor(border.getBorderColor());
                    break;
                case HORIZONTAL:
                    style.setBorderTop(border.getBorderStyle());
                    style.setBorderBottom(border.getBorderStyle());

                    style.setTopBorderColor(border.getBorderColor());
                    style.setBottomBorderColor(border.getBorderColor());
                    break;
                case FULL:
                    style.setBorderLeft(border.getBorderStyle());
                    style.setBorderRight(border.getBorderStyle());
                    style.setBorderTop(border.getBorderStyle());
                    style.setBorderBottom(border.getBorderStyle());

                    style.setLeftBorderColor(border.getBorderColor());
                    style.setRightBorderColor(border.getBorderColor());
                    style.setTopBorderColor(border.getBorderColor());
                    style.setBottomBorderColor(border.getBorderColor());
                    break;
            }
        }

    }

    private XSSFColor parseColorHex(XSSFWorkbook workbook, String hex, String fallbackHex) {
        // https://stackoverflow.com/a/7471183
        Color color;
        try {
            color = Color.decode(hex);
        } catch (Exception e) {
            color = Color.decode(fallbackHex);
        }

        // https://stackoverflow.com/a/23490977
        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
        return new XSSFColor(color, colorMap);
    }

    Style cloneSelf() {
        return Style.builder()
                    .fontName(fontName).fontSize(fontSize)
                    .bold(bold).underline(underline)
                    .date(date).datePattern(datePattern)
                    .fontColorInHex(fontColorInHex)
                    .backgroundColorInHex(backgroundColorInHex)
                    .horizontalAlignment(horizontalAlignment)
                    .verticalAlignment(verticalAlignment)
                    .borders(borders)
                    .build();
    }

    public boolean isDate() {
        return date == Boolean.TRUE;
    }

    public static class StyleBuilder {
        private final Style style = new Style();

        public StyleBuilder fontName(String s) {
            style.fontName = s;
            return this;
        }

        public StyleBuilder fontSize(Short i) {
            style.fontSize = i;
            return this;
        }

        public StyleBuilder bold(Boolean b) {
            style.bold = b;
            return this;
        }

        public StyleBuilder underline(Boolean b) {
            style.underline = b;
            return this;
        }

        public StyleBuilder date(Boolean b) {
            style.date = b;
            return this;
        }

        public StyleBuilder datePattern(String s) {
            style.datePattern = s;
            return this;
        }

        public StyleBuilder fontColorInHex(String s) {
            style.fontColorInHex = s;
            return this;
        }

        public StyleBuilder backgroundColorInHex(String s) {
            style.backgroundColorInHex = s;
            return this;
        }

        public StyleBuilder horizontalAlignment(HorizontalAlignment alignment) {
            style.horizontalAlignment = alignment;
            return this;
        }

        public StyleBuilder verticalAlignment(VerticalAlignment alignment) {
            style.verticalAlignment = alignment;
            return this;
        }

        private StyleBuilder borders(LinkedHashSet<Border> borders) {
            style.borders = borders;
            return this;
        }

        private void addBorderSafely(Border border) {
            if (style.borders == null) {
                style.borders = new LinkedHashSet<>();
            }
            style.borders.add(border);
        }

        @SneakyThrows
        public StyleBuilder border(BorderSide borderSide) {
            addBorderSafely(new Border(borderSide));
            return this;
        }

        @SneakyThrows
        public StyleBuilder border(BorderSide borderSide, String borderColor) {
            addBorderSafely(new Border(borderSide, borderColor));
            return this;
        }

        @SneakyThrows
        public StyleBuilder border(BorderSide borderSide, String borderColor, BorderStyle borderStyle) {
            addBorderSafely(new Border(borderSide, borderColor, borderStyle));
            return this;
        }

        public Style build() {
            return style;
        }
    }
}
