package io.github.nambach.excelutil.style;

import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.Arrays;
import java.util.Collection;
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

    public CellStyle accumulate(Style... styles) {
        return accumulate(Arrays.asList(styles));
    }

    public CellStyle accumulate(Collection<Style> styles) {
        if (styles == null || styles.isEmpty()) {
            return null;
        }
        if (styles.stream().allMatch(Objects::isNull)) {
            return null;
        }

        // Check cache
        List<String> idPath = styles
                .stream().filter(Objects::nonNull)
                .map(Style::getUuid)
                .collect(Collectors.toList());
        Node<CellStyle> node = root.lookup(idPath);
        if (node != null && node.getData() != null) {
            return node.getData();
        }

        // Accumulate styles
        Style combinedStyle = styles.stream()
                                    .filter(Objects::nonNull)
                                    .reduce(Style::accumulate)
                                    .orElse(null);
        if (combinedStyle == null) {
            return null;
        }

        // Create new style; Save to cache
        CellStyle style = combinedStyle.toHandler(this).renderCellStyle();
        root.updatePath(idPath, style);
        return style;
    }
}
