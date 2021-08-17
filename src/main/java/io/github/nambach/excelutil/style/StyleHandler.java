package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.List;

import static io.github.nambach.excelutil.style.StyleProperty.Borders;

interface StyleHandler {
    CellStyle renderCellStyle(Style style);

    void processFont(Style style, CellStyle cellStyle);

    void setBorderTop(Border border, CellStyle cellStyle);

    void setBorderBottom(Border border, CellStyle cellStyle);

    void setBorderLeft(Border border, CellStyle cellStyle);

    void setBorderRight(Border border, CellStyle cellStyle);

    @SuppressWarnings({"unchecked"})
    default void processBorder(Style style, CellStyle cellStyle) {
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
}
