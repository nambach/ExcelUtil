package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.style.Style;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.util.CellAddress;

import java.util.Date;
import java.util.function.Function;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class WriterCell {
    private String content;
    private Double value;
    private Date date;
    private String datePattern;
    private int rowAt;
    private int colAt;
    private int rowSpan = 1;
    private int colSpan = 1;
    private Style style;

    WriterCell(CellAddress address, Style style) {
        this.rowAt = address.getRow();
        this.colAt = address.getColumn();
        this.style = style;
    }

    public WriterCell text(String s) {
        this.content = s;
        return this;
    }

    public WriterCell number(double v) {
        this.value = v;
        return this;
    }

    public WriterCell date(Date d) {
        this.date = d;
        this.datePattern = "MMM dd, yyyy";
        return this;
    }

    public WriterCell date(Date d, String datePattern) {
        this.date = d;
        this.datePattern = datePattern;
        return this;
    }

    public WriterCell colSpan(int v) {
        if (v > 1) {
            this.colSpan = v;
        }
        return this;
    }

    public WriterCell rowSpan(int v) {
        if (v > 1) {
            this.rowSpan = v;
        }
        return this;
    }

    public WriterCell replaceStyle(Style style) {
        this.style = style;
        return this;
    }

    public WriterCell style(Function<Style.StyleBuilder, Style.StyleBuilder> f) {
        if (f != null) {
            // Create new copied style, not using the current reference
            Style.StyleBuilder builder = Style.builder(this.style);
            f.apply(builder);
            this.style = builder.build();
        }
        return this;
    }
}
