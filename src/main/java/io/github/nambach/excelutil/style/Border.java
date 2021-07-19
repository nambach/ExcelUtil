package io.github.nambach.excelutil.style;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.BorderStyle;

@Getter
@Setter
public class Border {

    private BorderSide side;
    private String hexColor;
    private BorderStyle borderStyle;

    public Border(BorderSide side) {
        this.side = side;
        this.hexColor = StyleConstant.BLACK;
        this.borderStyle = BorderStyle.THIN;
    }

    public Border(BorderSide side, String hexColor) {
        this.side = side;
        this.hexColor = hexColor;
        this.borderStyle = BorderStyle.THIN;
    }

    public Border(BorderSide side, String hexColor, BorderStyle borderStyle) {
        this.side = side;
        this.hexColor = hexColor;
        this.borderStyle = borderStyle;
    }
}
