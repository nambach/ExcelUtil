package io.github.nambach.excelutil.style;

import io.github.nambach.excelutil.util.Node;
import lombok.Getter;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CacheStyle {
    private final Node<XSSFCellStyle> root = new Node<>("root", null);
    private final XSSFWorkbook workbook;

    public CacheStyle(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public boolean containStyle(String id) {
        Node<XSSFCellStyle> child = root.getChild(id);
        return child != null && child.getData() != null;
    }

    public XSSFCellStyle get(String id) {
        return root.getChild(id).getData();
    }

    public void put(String id, XSSFCellStyle style) {
        root.addChild(id, style);
    }

    public Style unsafeAccumulate(Style source, Style extra) {
        if (source == extra) {
            return source;
        }
        if (source == null) {
            return extra;
        }
        if (extra == null) {
            return source;
        }

        Style clone = source.cloneSelf();
        if (extra.fontName != null) {
            clone.fontName = extra.fontName;
        }
        if (extra.fontSize != null) {
            clone.fontSize = extra.fontSize;
        }
        if (extra.bold != null) {
            clone.bold = extra.bold;
        }
        if (extra.underline != null) {
            clone.underline = extra.underline;
        }
        if (extra.date != null) {
            clone.date = extra.date;
        }
        if (extra.datePattern != null) {
            clone.datePattern = extra.datePattern;
        }
        if (extra.fontColorInHex != null) {
            clone.fontColorInHex = extra.fontColorInHex;
        }
        if (extra.backgroundColorInHex != null) {
            clone.backgroundColorInHex = extra.backgroundColorInHex;
        }
        if (extra.horizontalAlignment != null) {
            clone.horizontalAlignment = extra.horizontalAlignment;
        }
        if (extra.verticalAlignment != null) {
            clone.verticalAlignment = extra.verticalAlignment;
        }
        if (extra.borders != null) {
            clone.borders = extra.borders;
        }
        return clone;
    }

    public XSSFCellStyle accumulate(Style... styles) {
        if (styles.length == 0) {
            return null;
        }
        if (Arrays.stream(styles).allMatch(Objects::isNull)) {
            return null;
        }

        // Check cache
        List<String> idPath = Arrays
                .stream(styles).filter(Objects::nonNull)
                .map(Style::getUuid)
                .collect(Collectors.toList());
        Node<XSSFCellStyle> node = root.lookup(idPath);
        if (node != null && node.getData() != null) {
            return node.getData();
        }

        // Create style and add to cache
        Style combinedStyle = Arrays.stream(styles).reduce(null, this::unsafeAccumulate);
        if (combinedStyle == null) {
            return null;
        }
        XSSFCellStyle style = combinedStyle.processWithoutCache(workbook);
        root.updatePath(idPath, style);
        return style;
    }
}
