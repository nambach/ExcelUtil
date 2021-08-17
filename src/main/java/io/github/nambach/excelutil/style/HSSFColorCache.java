package io.github.nambach.excelutil.style;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import java.util.HashMap;
import java.util.stream.Stream;

public class HSSFColorCache {

    private static final short minIndex = Stream.of(HSSFColor.HSSFColorPredefined.values())
                                                .map(HSSFColor.HSSFColorPredefined::getIndex)
                                                .min(Short::compareTo).get();

    private static final short maxIndex = Stream.of(HSSFColor.HSSFColorPredefined.values())
                                                .map(HSSFColor.HSSFColorPredefined::getIndex)
                                                .max(Short::compareTo).get();

    private final HSSFWorkbook workbook;
    private final HSSFPalette palette;
    private final HashMap<Integer, Short> cache = new HashMap<>(); // RGB to palette index
    private Policy policy;

    public HSSFColorCache(HSSFWorkbook workbook, Policy policy) {
        this.workbook = workbook;
        this.palette = workbook.getCustomPalette();
        this.policy = policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public short getIndex(StyleColor color) {
        HSSFPalette palette = workbook.getCustomPalette();
        switch (policy) {
            case USE_MOST_SIMILAR:
                if (color.isPreset()) {
                    return color.getHssfColor().getIndex();
                }
                return palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue())
                              .getIndex();

            case OVERRIDE:
                int rgb = color.getRGB();

                // Check cache
                Short index = cache.get(rgb);
                if (index != null) {
                    return index;
                }

                // Put to cache
                index = findNextIndex();
                overridePalette(index, color.getTriplet());
                cache.put(rgb, index);
                return index;
        }
        return HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex();
    }

    private void overridePalette(short index, short[] rgb) {
        palette.setColorAtIndex(index,
                                (byte) rgb[0],
                                (byte) rgb[1],
                                (byte) rgb[2]);
    }

    private short findNextIndex() {
        for (short i = minIndex; i <= maxIndex; i++) {
            if (!cache.containsValue(i)) {
                return i;
            }
        }
        throw new RuntimeException("Exceeds limit colors of HSSFWorkbook");
    }

    int size() {
        return cache.size();
    }

    public enum Policy {
        OVERRIDE,
        USE_MOST_SIMILAR
    }
}
