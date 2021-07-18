package io.github.nambach.excelutil.style;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

@Getter
@Setter
public class Border {

    private BorderSide borderSide;
    private XSSFColor borderColor;
    private BorderStyle borderStyle;

    public Border(BorderSide borderSide) throws DecoderException {
        this.borderSide = borderSide;
        this.borderColor = new XSSFColor(Hex.decodeHex("000000".toCharArray()), null);
        this.borderStyle = BorderStyle.THIN;
    }

    public Border(BorderSide borderSide, String hexColor) throws DecoderException {
        this.borderSide = borderSide;
        this.borderColor = new XSSFColor(Hex.decodeHex(hexColor.replace("#", "").toCharArray()), null);
        this.borderStyle = BorderStyle.THIN;
    }

    public Border(BorderSide borderSide, String hexColor, BorderStyle borderStyle) throws DecoderException {
        this.borderSide = borderSide;
        this.borderColor = new XSSFColor(Hex.decodeHex(hexColor.replace("#", "").toCharArray()), null);
        this.borderStyle = borderStyle;
    }
}
