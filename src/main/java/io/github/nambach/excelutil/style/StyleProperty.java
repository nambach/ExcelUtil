package io.github.nambach.excelutil.style;

import io.github.nambach.excelutil.util.ReadableValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@Getter
@AllArgsConstructor
class StyleProperty implements ReadableValue {
    static final StyleProperty FontName = new StyleProperty("fontName", null);
    static final StyleProperty FontSize = new StyleProperty("fontSize", null);
    static final StyleProperty Bold = new StyleProperty("bold", null);
    static final StyleProperty Underline = new StyleProperty("underline", null);
    static final StyleProperty Indentation = new StyleProperty("indentation", null);
    static final StyleProperty WrapText = new StyleProperty("wrapText", null);
    static final StyleProperty DatePattern = new StyleProperty("datePattern", null);
    static final StyleProperty FontColorInHex = new StyleProperty("fontColorInHex", null);
    static final StyleProperty BackgroundColorInHex = new StyleProperty("backgroundColorInHex", null);
    static final StyleProperty Alignments = new StyleProperty("alignments", null);
    static final StyleProperty Borders = new StyleProperty("borders", null);

    private final String name;
    @With
    private final Object value;

    public StyleProperty makeCopy() {
        return new StyleProperty(name, this.createCopy());
    }
}
