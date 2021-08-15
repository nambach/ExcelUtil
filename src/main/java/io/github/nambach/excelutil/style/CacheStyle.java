package io.github.nambach.excelutil.style;

import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CacheStyle {
    private final Node<CellStyle> root = new Node<>("root", null);
    private final Workbook workbook;

    // For XSSF format
    private final Map<String, XSSFColor> colorCache = new HashMap<>();

    public CacheStyle(Workbook workbook) {
        this.workbook = workbook;
    }

    public String printTotalStyle() {
        int total = root.countAllChildren();
        return String.format("%d %s created.", total, total > 1 ? "styles were" : "style was");
    }

    public boolean containStyle(String id) {
        Node<CellStyle> child = root.getChild(id);
        return child != null && child.getData() != null;
    }

    public CellStyle get(String id) {
        return root.getChild(id).getData();
    }

    public void put(String id, CellStyle style) {
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

        Style clone = source.makeCopy();
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
        if (extra.indentation != null) {
            clone.indentation = extra.indentation;
        }
        if (extra.wrapText != null) {
            clone.wrapText = extra.wrapText;
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

    public CellStyle accumulate(Style... styles) {
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
        Node<CellStyle> node = root.lookup(idPath);
        if (node != null && node.getData() != null) {
            return node.getData();
        }

        // Create style and add to cache
        Style combinedStyle = Arrays.stream(styles).reduce(null, this::unsafeAccumulate);
        if (combinedStyle == null) {
            return null;
        }
        CellStyle style = combinedStyle.toHandler(this).renderCellStyle(workbook);
        root.updatePath(idPath, style);
        return style;
    }
}
