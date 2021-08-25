package io.github.nambach.excelutil.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class WriterComment {
    private String content;
    private String author;
    private int rowOffset;
    private int colOffset;
    private int rowSpan = 2;
    private int colSpan = 1;

    WriterComment() {
    }

    public WriterComment content(String s) {
        this.content = s;
        return this;
    }

    public WriterComment author(String s) {
        this.author = s;
        return this;
    }

    public WriterComment rowOffset(int i) {
        this.rowOffset = i;
        return this;
    }

    public WriterComment colOffset(int i) {
        this.colOffset = i;
        return this;
    }

    public WriterComment rowSpan(int i) {
        if (i > 0) {
            this.rowSpan = i;
        }
        return this;
    }

    public WriterComment colSpan(int i) {
        if (i > 0) {
            this.colSpan = i;
        }
        return this;
    }
}
