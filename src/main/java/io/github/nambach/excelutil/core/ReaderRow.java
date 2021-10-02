package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.validator.ObjectError;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;

public class ReaderRow extends ReaderController {
    private final Row row;

    @Getter(AccessLevel.PACKAGE)
    private boolean skipThisObject;

    ReaderRow(Row row, ReaderConfig<?> config, Result<?> result) {
        super(config, result);
        this.row = row;
    }

    public void skipThisObject() {
        this.skipThisObject = true;
    }

    public void setObjectError(ObjectError objectError) {
        result.newRowError(row.getRowNum()).setObjectError(objectError);
    }

    @Override
    public void setError(String message) {
        result.newRowError(row.getRowNum()).setCustomError(message);
    }

    @Override
    public void throwError(String message) {
        this.setError(message);
        super.terminateNow();
    }
}
