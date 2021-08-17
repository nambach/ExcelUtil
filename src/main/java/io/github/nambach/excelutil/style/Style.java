package io.github.nambach.excelutil.style;

import io.github.nambach.excelutil.util.Copyable;
import io.github.nambach.excelutil.util.CopyableList;
import io.github.nambach.excelutil.util.Readable;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static io.github.nambach.excelutil.style.StyleProperty.Alignments;
import static io.github.nambach.excelutil.style.StyleProperty.BackgroundColor;
import static io.github.nambach.excelutil.style.StyleProperty.Bold;
import static io.github.nambach.excelutil.style.StyleProperty.Borders;
import static io.github.nambach.excelutil.style.StyleProperty.DatePattern;
import static io.github.nambach.excelutil.style.StyleProperty.FontColor;
import static io.github.nambach.excelutil.style.StyleProperty.FontName;
import static io.github.nambach.excelutil.style.StyleProperty.FontSize;
import static io.github.nambach.excelutil.style.StyleProperty.Indentation;
import static io.github.nambach.excelutil.style.StyleProperty.Underline;
import static io.github.nambach.excelutil.style.StyleProperty.WrapText;

public class Style implements Copyable<Style> {

    @Getter(AccessLevel.PUBLIC)
    private final String uuid;
    private final Map<String, StyleProperty> styleMap = new HashMap<>();

    private Style(String uuid) {
        this.uuid = uuid;
    }

    private static Style newRandomStyle() {
        return new Style(UUID.randomUUID().toString());
    }

    public static StyleBuilder builder() {
        return new StyleBuilder(newRandomStyle());
    }

    public static StyleBuilder builder(Style style) {
        if (style != null) {
            return new StyleBuilder(style.makeCopy());
        } else {
            return builder();
        }
    }

    void put(StyleProperty styleProperty) {
        styleMap.put(styleProperty.getName(), styleProperty);
    }

    StyleProperty getProperty(StyleProperty any) {
        return styleMap.getOrDefault(any.getName(), any);
    }

    StyleProperty getOrDefault(StyleProperty any) {
        StyleProperty property = styleMap.get(any.getName());

        if (property == null) {
            put(any);
            property = any;
        }

        return property;
    }

    boolean hasNoProperty() {
        return styleMap.isEmpty() || styleMap.values().stream().allMatch(Readable::isNullOrEmpty);
    }

    /**
     * @return a shallow copied of this style
     */
    @Override
    public Style makeCopy() {
        Style copy = newRandomStyle();
        this.styleMap.forEach((name, property) -> {
            copy.styleMap.put(name, property.makeCopy());
        });
        return copy;
    }

    Style accumulate(Style other) {
        if (this == other || other == null || other.hasNoProperty()) {
            return this;
        }

        Style accumulated = this.makeCopy();
        other.styleMap.values().stream()
                      .filter(Readable::hasValue)
                      .forEach(accumulated::put);
        return accumulated;
    }

    public static class StyleBuilder {
        private final Style style;

        private StyleBuilder(Style style) {
            this.style = style;
        }

        public StyleBuilder fontName(String s) {
            style.put(FontName.withValue(s));
            return this;
        }

        public StyleBuilder fontSize(short i) {
            style.put(FontSize.withValue(i));
            return this;
        }

        public StyleBuilder bold(boolean b) {
            style.put(Bold.withValue(b));
            return this;
        }

        public StyleBuilder underline(boolean b) {
            style.put(Underline.withValue(b));
            return this;
        }

        public StyleBuilder indentation(short i) {
            style.put(Indentation.withValue(i));
            return this;
        }

        public StyleBuilder wrapText(boolean b) {
            style.put(WrapText.withValue(b));
            return this;
        }

        public StyleBuilder datePattern(String s) {
            style.put(DatePattern.withValue(s));
            return this;
        }

        public StyleBuilder fontColor(IndexedColors color) {
            style.put(FontColor.withValue(StyleColor.fromPredefined(color)));
            return this;
        }

        public StyleBuilder fontColorInHex(String hex) {
            style.put(FontColor.withValue(StyleColor.fromHex(hex)));
            return this;
        }

        public StyleBuilder fontColorInRGB(int r, int g, int b) {
            style.put(FontColor.withValue(StyleColor.fromRGB(r, g, b)));
            return this;
        }

        public StyleBuilder backgroundColor(IndexedColors color) {
            style.put(BackgroundColor.withValue(StyleColor.fromPredefined(color)));
            return this;
        }

        public StyleBuilder backgroundColorInHex(String hex) {
            style.put(BackgroundColor.withValue(StyleColor.fromHex(hex)));
            return this;
        }

        public StyleBuilder backgroundColorInRGB(int r, int g, int b) {
            style.put(BackgroundColor.withValue(StyleColor.fromRGB(r, g, b)));
            return this;
        }

        @SuppressWarnings({"unchecked"})
        public StyleBuilder horizontalAlignment(HorizontalAlignment alignment) {
            style.getOrDefault(Alignments.withValue(new CopyableList<>()))
                 .getAny(CopyableList.class)
                 .ifPresent(l -> l.add(alignment));
            return this;
        }

        @SuppressWarnings({"unchecked"})
        public StyleBuilder verticalAlignment(VerticalAlignment alignment) {
            style.getOrDefault(Alignments.withValue(new CopyableList<>()))
                 .getAny(CopyableList.class)
                 .ifPresent(l -> l.add(alignment));
            return this;
        }

        @SuppressWarnings({"unchecked"})
        private void addBorderSafely(Border border) {
            style.getOrDefault(Borders.withValue(new CopyableList<>()))
                 .getAny(CopyableList.class)
                 .ifPresent(l -> l.add(border));
        }

        public StyleBuilder border(BorderSide borderSide) {
            addBorderSafely(new Border().side(borderSide));
            return this;
        }

        public StyleBuilder border(Function<Border, Border> borderBuilder) {
            Border border = borderBuilder.apply(new Border());
            addBorderSafely(border);
            return this;
        }

        public Style build() {
            return style;
        }
    }
}
