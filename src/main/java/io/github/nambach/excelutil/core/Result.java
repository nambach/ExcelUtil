package io.github.nambach.excelutil.core;

import io.github.nambach.excelutil.util.ListUtil;
import io.github.nambach.excelutil.validator.Error;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Result<T> extends ArrayList<T> implements List<T> {
    @Getter(AccessLevel.NONE)
    private final Class<T> tClass;

    private final List<Raw<T>> rawData = new ArrayList<>();
    private final ReaderError error = new ReaderError();

    public Result(Class<T> tClass) {
        this.tClass = tClass;
    }

    public boolean hasError() {
        return ListUtil.hasMember(error);
    }

    void addRaw(Raw<T> raw) {
        rawData.add(raw);
        this.add(raw.getData());
    }

    void addError(int index, String fieldName, List<String> messages) {
        ReaderError.Line existed = error.stream().filter(l -> l.getIndex() == index).findFirst().orElse(null);
        if (existed != null) {
            existed.objectError.add(fieldName, messages);
        } else {
            Error objectError = new Error(tClass);
            objectError.add(fieldName, messages);
            ReaderError.Line newOne = new ReaderError.Line(index, objectError);
            error.add(newOne);
        }
    }

    void addError(int index, String fieldName, String message) {
        addError(index, fieldName, Collections.singletonList(message));
    }
}
