package io.github.nambach.excelutil.core;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;

public abstract class ReaderController {
    protected Result<?> result;

    @Getter(AccessLevel.PACKAGE)
    private boolean isExitNow;

    @Getter(AccessLevel.PACKAGE)
    private boolean isEarlyExit;

    ReaderController(ReaderConfig<?> config, Result<?> result) {
        if (config != null) {
            this.isEarlyExit = config.isEarlyExit();
        }
        this.result = result;
    }

    public boolean hasError() {
        return result.hasErrors();
    }

    public List<RowError> getErrors() {
        return result.getErrors();
    }

    public abstract void setError(String message);

    public abstract void throwError(String message);

    public void terminateNow() {
        this.isExitNow = true;
    }
}
