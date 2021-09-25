package io.github.nambach.excelutil.style;

import lombok.EqualsAndHashCode;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;

/**
 * Key as RGB value
 */
@EqualsAndHashCode(callSuper = true)
public class XSSFColorCache extends HashMap<Integer, XSSFColor> {
    private final transient XSSFWorkbook workbook;

    public XSSFColorCache(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public XSSFColor getColor(StyleColor color) {
        int key = color.getRGB();
        if (containsKey(key)) {
            return get(key);
        }

        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
        XSSFColor rs = new XSSFColor(color, colorMap);

        // save cache
        put(key, rs);
        return rs;
    }
}
