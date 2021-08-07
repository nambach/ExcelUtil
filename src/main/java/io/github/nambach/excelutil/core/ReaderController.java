package io.github.nambach.excelutil.core;

public class ReaderController {
    protected String error;
    protected boolean earlyExist;

    public void setError(String message) {
        this.error = message;
    }

    public void throwError(String message) {
        this.error = message;
        this.earlyExist = true;
    }

    protected boolean hasError() {
        return error != null;
    }

    protected void clearError() {
        this.error = null;
    }

    protected String getError(String address) {
        if (address != null) {
            return String.format("%s: %s", address, error);
        }
        return error;
    }


}
