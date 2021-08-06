package io.github.nambach.excelutil.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
class FlatData extends ArrayList<Object> {
    private final String id;

    FlatData() {
        id = UUID.randomUUID().toString();
    }

    private FlatData(String id) {
        this.id = id;
    }

    public FlatData cloneSelf() {
        FlatData clone = new FlatData(this.id);
        clone.addAll(this);
        return clone;
    }

    public Object getLast() {
        if (isEmpty()) return null;
        return get(size() - 1);
    }
}
