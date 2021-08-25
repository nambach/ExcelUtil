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
    private int rowAt;
    private int colAt;
    private int rowSpan = 1;
    private int colSpan = 1;
    private Style style;
    private WriterComment comment;

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
        return this;
    }

    public WriterCell date(Date d, String datePattern) {
        this.date = d;
        if (datePattern != null) {
            this.style(s -> s.datePattern(datePattern));
        }
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

    public WriterCell comment(String comment) {
        return this.comment(c -> c.content(comment));
    }

    public WriterCell comment(String comment, String author) {
        return this.comment(c -> c.content(comment).author(author));
    }

    public WriterCell comment(Function<WriterComment, WriterComment> builder) {
        this.comment = builder.apply(new WriterComment());
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
