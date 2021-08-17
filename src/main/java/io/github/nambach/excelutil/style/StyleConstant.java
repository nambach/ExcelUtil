package io.github.nambach.excelutil.style;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

public class StyleConstant {

    static final String FONT_NAME = "Times New Roman";
    public static final Style TIMES_NEW_ROMAN = Style
            .builder()
            .fontName(FONT_NAME)
            .build();
    public static final Style DATA_STYLE = Style
            .builder()
            .fontName(FONT_NAME)
            .border(BorderSide.FULL)
            .build();
    public static final Style HEADER_STYLE = Style
            .builder()
            .fontName(FONT_NAME)
            .bold(true)
            .backgroundColor(IndexedColors.YELLOW)
            .border(BorderSide.FULL)
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .build();
    static final StyleColor BLACK = StyleColor.fromPredefined(IndexedColors.BLACK);
    static final StyleColor WHITE = StyleColor.fromPredefined(IndexedColors.WHITE);
}
