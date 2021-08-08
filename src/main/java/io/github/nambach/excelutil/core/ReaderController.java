package io.github.nambach.excelutil.core;

import lombok.AccessLevel;
import lombok.Getter;

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

    public abstract void setError(String message);

    public abstract void throwError(String message);

    protected void terminateNow() {
        this.isExitNow = true;
    }
}
