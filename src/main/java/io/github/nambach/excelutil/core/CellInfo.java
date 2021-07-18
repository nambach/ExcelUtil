package io.nambm.excel.core;

import io.nambm.excel.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.function.Function;

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
    private Style style;

    CellInfo(Pointer pointer, Style style) {
        this.rowAt = pointer.getRow();
        this.colAt = pointer.getCol();
        this.style = style;
    }

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

    public CellInfo style(Style style) {
        this.style = style;
        return this;
    }

    public CellInfo style(Function<Style.StyleBuilder, Style.StyleBuilder> f) {
        if (f != null) {
            // Create new copied style, not using the current reference
            Style.StyleBuilder builder = Style.builder(this.style);
            f.apply(builder);
            this.style = builder.build();
        }
        return this;
    }
}
