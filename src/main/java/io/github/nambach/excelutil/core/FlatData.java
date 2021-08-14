package io.github.nambach.excelutil.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
class FlatData extends ArrayList<Object> {

    FlatData() {
    }

    public FlatData makeCopy() {
        FlatData clone = new FlatData();
        clone.addAll(this);
        return clone;
    }

    public Object getLast() {
        if (isEmpty()) return null;
        return get(size() - 1);
    }
}
