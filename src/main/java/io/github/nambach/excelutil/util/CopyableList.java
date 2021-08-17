package io.github.nambach.excelutil.util;

import java.util.ArrayList;

public class CopyableList<T> extends ArrayList<T> implements Copyable<CopyableList<T>> {

    @Override
    public CopyableList<T> makeCopy() {
        CopyableList<T> list = new CopyableList<>();
        list.addAll(this);
        return list;
    }
}
