package io.github.nambach.excelutil.style;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

@Getter
@Setter
public class Border {

    private BorderSide side = BorderSide.NONE;
    private StyleColor color = StyleColor.fromPredefined(IndexedColors.BLACK);
    private BorderStyle borderStyle = BorderStyle.THIN;

    public Border side(BorderSide side) {
        this.side = side;
        return this;
    }

    public Border hexColor(String hex) {
        this.color = StyleColor.fromHex(hex);
        return this;
    }

    public Border rgbColor(int r, int g, int b) {
        this.color = StyleColor.fromRGB(r, g, b);
        return this;
    }

    public Border color(IndexedColors predefinedColor) {
        this.color = StyleColor.fromPredefined(predefinedColor);
        return this;
    }

    public Border style(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
        return this;
    }
}
