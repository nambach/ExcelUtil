package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ListUtil;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.nambach.excelutil.util.ListUtil.findElse;

@Getter
public class Result<T> extends ArrayList<T> implements List<T> {
    @Getter(AccessLevel.NONE)
    private final Class<T> tClass;

    private final List<Raw<T>> rawData = new ArrayList<>();
    private final List<LineError> errors = new ArrayList<>();

    public Result(Class<T> tClass) {
        this.tClass = tClass;
    }

    public boolean hasError() {
        return ListUtil.hasMember(errors);
    }

    void addRaw(Raw<T> raw) {
        rawData.add(raw);
        this.add(raw.getData());
    }

    void addError(int index, String fieldName, List<String> messages) {
        LineError lineError = findElse(errors, l -> l.getIndex() == index, new LineError(index, tClass));
        lineError.appendError(fieldName, messages);
    }

    void addError(int index, String fieldName, String message) {
        addError(index, fieldName, Collections.singletonList(message));
    }
}
