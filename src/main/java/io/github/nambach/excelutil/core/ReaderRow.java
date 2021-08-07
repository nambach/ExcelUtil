package io.github.nambach.excelutil.core;

public class ReaderRow extends ReaderController {
    boolean skipThisObject;

    public void skipThisObject() {
        this.skipThisObject = true;
    }
}
