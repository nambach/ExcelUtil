package io.github.nambach.excelutil.style;

import io.github.nambach.excelutil.util.Copyable;
import lombok.Getter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

class StyleColor extends Color implements Copyable<StyleColor>, Comparable<StyleColor> {

    private static final Map<Integer, HSSFColor.HSSFColorPredefined> PredefinedMap = new HashMap<Integer, HSSFColor.HSSFColorPredefined>() {{
        for (HSSFColor.HSSFColorPredefined value : HSSFColor.HSSFColorPredefined.values()) {
            put((int) value.getIndex(), value);
        }
    }};

    @Getter
    private final HSSFColor hssfColor;

    private StyleColor(int r, int g, int b, HSSFColor hssfColor) {
        super(r, g, b);
        this.hssfColor = hssfColor;
    }

    private StyleColor(int rgb, HSSFColor hssfColor) {
        super(rgb);
        this.hssfColor = hssfColor;
    }

    public static StyleColor fromPredefined(IndexedColors color) {
        HSSFColor predefined = PredefinedMap.get((int) color.index).getColor();
        short[] rgb = predefined.getTriplet();
        return new StyleColor(rgb[0], rgb[1], rgb[2], predefined);
    }

    public static StyleColor fromRGB(int r, int g, int b) {
        HSSFColor predefined = findInMap((short) r, (short) g, (short) b);
        return new StyleColor(r, g, b, predefined);
    }

    public static StyleColor fromHex(String hexColor) {
        Color color = Color.decode(hexColor);
        HSSFColor predefined = findInMap((short) color.getRed(), (short) color.getGreen(), (short) color.getBlue());
        return new StyleColor(color.getRGB(), predefined);
    }

    private static HSSFColor findInMap(short r, short g, short b) {
        for (HSSFColor.HSSFColorPredefined value : PredefinedMap.values()) {
            short[] rgb = value.getTriplet();
            if (rgb[0] == r && rgb[1] == g && rgb[2] == b) {
                return value.getColor();
            }
        }
        return null;
    }

    @Override
    public StyleColor makeCopy() {
        return new StyleColor(this.getRGB(), hssfColor);
    }

    IndexedColors toIndexedColor() {
        if (hssfColor != null) {
            return IndexedColors.fromInt(hssfColor.getIndex());
        }
        return null;
    }

    public boolean isPreset() {
        return hssfColor != null;
    }

    public boolean isCustom() {
        return hssfColor == null;
    }

    @Override
    public int compareTo(StyleColor o) {
        return this.getRGB() - o.getRGB();
    }

    public short[] getTriplet() {
        return new short[]{(short) getRed(), (short) getGreen(), (short) getBlue()};
    }
}
