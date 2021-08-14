package io.github.nambach.excelutil.style;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.With;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.LinkedHashSet;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Style {

    @Getter(AccessLevel.PUBLIC)
    @With(AccessLevel.PRIVATE)
    private final String uuid;

    // Text
    String fontName;
    Short fontSize;
    Boolean bold;
    Boolean underline;
    Short indentation;
    Boolean wrapText;

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

    private Style(String uuid) {
        this.uuid = uuid;
    }

    public static StyleBuilder builder() {
        return new StyleBuilder(new Style(UUID.randomUUID().toString()));
    }

    public static StyleBuilder builder(Style style) {
        if (style != null) {
            return new StyleBuilder(style.makeCopy());
        } else {
            return builder();
        }
    }

    public StyleHandler toHandler(CacheStyle cache) {
        if (cache.getWorkbook() instanceof XSSFWorkbook) {
            return new XSSFStyleHandler(this, cache.getColorCache());
        }
        return null;
    }

    /**
     * @return a shallow copied of this style
     */
    Style makeCopy() {
        return this.withUuid(UUID.randomUUID().toString());
    }

    public boolean isDate() {
        return date == Boolean.TRUE;
    }

    public static class StyleBuilder {
        private final Style style;

        private StyleBuilder(Style style) {
            this.style = style;
        }

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

        public StyleBuilder indentation(Short i) {
            style.indentation = i;
            return this;
        }

        public StyleBuilder wrapText(Boolean b) {
            style.wrapText = b;
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
