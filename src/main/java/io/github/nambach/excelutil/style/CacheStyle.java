package io.github.nambach.excelutil.style;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CacheStyle {
    private final Node<CellStyle> root = new Node<>("root", null);
    private final Workbook workbook;

    private final StyleHandler handler;

    @SneakyThrows
    public CacheStyle(Workbook workbook) {
        this.workbook = workbook;
        if (workbook instanceof XSSFWorkbook) {
            XSSFWorkbook wb = (XSSFWorkbook) workbook;
            handler = new XSSFStyleHandler(wb, new XSSFColorCache(wb));
        } else {
            throw new Exception("Unsupported workbook type");
        }
    }

    public String printTotalStyle() {
        int total = root.countAllChildren();
        String report = String.format("%d %s created.", total, total > 1 ? "styles were" : "style was");
        if (handler instanceof XSSFStyleHandler) {
            int totalColors = ((XSSFStyleHandler) handler).countColors();
            String colorReport = String.format("%d %s created.", totalColors, totalColors > 1 ? "colors were" : "color was");
            report += "\n" + colorReport;
        }
        return report;
    }

    public CellStyle accumulate(Style... styles) {
        return accumulate(Arrays.asList(styles));
    }

    public CellStyle accumulate(Collection<Style> src) {
        if (src == null) {
            return null;
        }

        List<Style> styles = src.stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (styles.isEmpty()) {
            return null;
        }

        // Check cache
        List<String> idPath = styles.stream().map(Style::getUuid).collect(Collectors.toList());
        Node<CellStyle> node = root.lookup(idPath);
        if (node != null && node.getData() != null) {
            return node.getData();
        }

        // Accumulate styles
        Style combinedStyle = styles.stream().reduce(Style::accumulate).orElse(null);

        // Render new style
        CellStyle style = handler.renderCellStyle(combinedStyle);

        // Save to cache
        root.updatePath(idPath, style);
        return style;
    }
}
