package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;

import static io.github.nambach.excelutil.style.StyleProperty.Alignments;
import static io.github.nambach.excelutil.style.StyleProperty.BackgroundColor;
import static io.github.nambach.excelutil.style.StyleProperty.Bold;
import static io.github.nambach.excelutil.style.StyleProperty.Borders;
import static io.github.nambach.excelutil.style.StyleProperty.DataFormat;
import static io.github.nambach.excelutil.style.StyleProperty.FontColor;
import static io.github.nambach.excelutil.style.StyleProperty.FontName;
import static io.github.nambach.excelutil.style.StyleProperty.FontSize;
import static io.github.nambach.excelutil.style.StyleProperty.Indentation;
import static io.github.nambach.excelutil.style.StyleProperty.Underline;
import static io.github.nambach.excelutil.style.StyleProperty.WrapText;

abstract class StyleHandler {
    abstract Workbook getWorkbook();

    public CellStyle renderCellStyle(Style style) {
        CellStyle cellStyle = getWorkbook().createCellStyle();
        DataFormat format = getWorkbook().createDataFormat();

        Font font = renderFont(style);
        cellStyle.setFont(font);

        processBorder(style, cellStyle);

        style.getProperty(Indentation).getShort().ifPresent(cellStyle::setIndention);
        style.getProperty(WrapText).getBoolean().ifPresent(cellStyle::setWrapText);
        style.getProperty(DataFormat).getString().ifPresent(pattern -> cellStyle.setDataFormat(format.getFormat(pattern)));

        style.getProperty(BackgroundColor).getAny(StyleColor.class).ifPresent(color -> {
            if (color.isPreset()) {
                cellStyle.setFillForegroundColor(color.toIndexedColor().index);
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

        return cellStyle;
    }

    protected Font renderFont(Style style) {
        Font font = getWorkbook().createFont();

        style.getProperty(FontName).getString().ifPresent(font::setFontName);
        style.getProperty(FontSize).getShort().ifPresent(font::setFontHeightInPoints);
        style.getProperty(Bold).getBoolean().ifPresent(font::setBold);
        style.getProperty(Underline).getBoolean().ifPresent(bool -> font.setUnderline(FontUnderline.SINGLE.getByteValue()));
        style.getProperty(FontColor).getAny(StyleColor.class).ifPresent(color -> {
            if (color.isPreset()) {
                font.setColor(color.toIndexedColor().index);
            }
        });

        return font;
    }

    abstract void setBorderTop(Border border, CellStyle cellStyle);

    abstract void setBorderBottom(Border border, CellStyle cellStyle);

    abstract void setBorderLeft(Border border, CellStyle cellStyle);

    abstract void setBorderRight(Border border, CellStyle cellStyle);

    @SuppressWarnings({"unchecked"})
    protected void processBorder(Style style, CellStyle cellStyle) {
        List<Border> borders = style.getProperty(Borders).getAny(List.class).orElse(null);
        if (borders == null) {
            return;
        }
        for (Border border : borders) {
            switch (border.getSide()) {
                case NONE:
                    cellStyle.setBorderTop(BorderStyle.NONE);
                    cellStyle.setBorderBottom(BorderStyle.NONE);
                    cellStyle.setBorderLeft(BorderStyle.NONE);
                    cellStyle.setBorderRight(BorderStyle.NONE);
                    return;
                case TOP:
                    setBorderTop(border, cellStyle);
                    break;
                case BOTTOM:
                    setBorderBottom(border, cellStyle);
                    break;
                case LEFT:
                    setBorderLeft(border, cellStyle);
                    break;
                case RIGHT:
                    setBorderRight(border, cellStyle);
                    break;
                case FULL:
                    setBorderTop(border, cellStyle);
                    setBorderBottom(border, cellStyle);
                    setBorderLeft(border, cellStyle);
                    setBorderRight(border, cellStyle);
                    break;
            }
        }
    }

    abstract public int countColors();
}
