package io.nambm.excel.style;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class DefaultStyle {
    static final String FONT_NAME = "Times New Roman";
    static final String YELLOW = "#FFFF00";
    static final String BLACK = "#000000";
    static final String WHITE = "#ffffff";

    public static final Style HEADER_STYLE = Style
            .builder()
            .fontName(FONT_NAME)
            .bold(true)
            .backgroundColorInHex(YELLOW)
            .border(BorderSide.FULL)
            .horizontalAlignment(HorizontalAlignment.CENTER)
            .build();

    public static final Style DATA_STYLE = Style
            .builder()
            .fontName(FONT_NAME)
            .border(BorderSide.FULL)
            .build();

    public static final Style DATE = Style
            .builder()
            .date(true)
            .build();
}
