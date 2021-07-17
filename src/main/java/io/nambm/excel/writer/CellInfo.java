package io.nambm.excel.writer;

import io.nambm.excel.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.poi.ss.util.CellAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class CellInfo {
    private String content;
    private Double value;
    private Date date;
    private String datePattern;
    private int rowAt;
    private int colAt;
    private int rowSpan = 1;
    private int colSpan = 1;
    private List<Style> styles = new ArrayList<>();

    public CellInfo text(String s) {
        this.content = s;
        return this;
    }

    public CellInfo number(double v) {
        this.value = v;
        return this;
    }

    public CellInfo date(Date d) {
        this.date = d;
        this.datePattern = "MMM dd, yyyy";
        return this;
    }

    public CellInfo date(Date d, String datePattern) {
        this.date = d;
        this.datePattern = datePattern;
        return this;
    }

    @SneakyThrows
    public CellInfo at(int rowAt, int colAt) {
        if (rowAt < 0 || colAt < 0) {
            throw new Exception("Cell coordinate is negative.");
        }
        this.rowAt = rowAt;
        this.colAt = colAt;
        return this;
    }

    @SneakyThrows
    public CellInfo at(String address) {
        try {
            CellAddress cellAddress = new CellAddress(address);
            this.at(cellAddress.getRow(), cellAddress.getColumn());
        } catch (Exception e) {
            throw new Exception("Error while parsing cell address: ", e);
        }
        return this;
    }

    public CellInfo colSpan(int v) {
        if (v > 1) {
            this.colSpan = v;
        }
        return this;
    }

    public CellInfo rowSpan(int v) {
        if (v > 1) {
            this.rowSpan = v;
        }
        return this;
    }

    public CellInfo style(Style... style) {
        if (style != null) {
            this.styles.addAll(Arrays.stream(style).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        return this;
    }
}
