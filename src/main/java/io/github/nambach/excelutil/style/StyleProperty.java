package io.github.nambach.excelutil.style;

import io.github.nambach.excelutil.util.Copyable;
import io.github.nambach.excelutil.util.Readable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

@Getter
@AllArgsConstructor
class StyleProperty implements Readable, Copyable<StyleProperty> {
    static final StyleProperty FontName = new StyleProperty("fontName", null);
    static final StyleProperty FontSize = new StyleProperty("fontSize", null);
    static final StyleProperty Bold = new StyleProperty("bold", null);
    static final StyleProperty Underline = new StyleProperty("underline", null);
    static final StyleProperty Indentation = new StyleProperty("indentation", null);
    static final StyleProperty WrapText = new StyleProperty("wrapText", null);
    static final StyleProperty DatePattern = new StyleProperty("datePattern", null);
    static final StyleProperty FontColor = new StyleProperty("fontColor", null);
    static final StyleProperty BackgroundColor = new StyleProperty("backgroundColor", null);
    static final StyleProperty Alignments = new StyleProperty("alignments", null);
    static final StyleProperty Borders = new StyleProperty("borders", null);

    private final String name;
    @With
    private final Object value;

    @Override
    public StyleProperty makeCopy() {
        return new StyleProperty(name, this.copyValue());
    }
}
