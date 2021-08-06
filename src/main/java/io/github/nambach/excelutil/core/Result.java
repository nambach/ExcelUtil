package io.github.nambach.excelutil.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Result<T> extends ArrayList<T> implements List<T> {
    private final List<Raw<T>> rawData = new ArrayList<>();

    public Result() {
    }

    void addRaw(Raw<T> raw) {
        rawData.add(raw);
        this.add(raw.getData());
    }
}
