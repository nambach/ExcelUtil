package io.github.nambach.excelutil.util;

import java.util.HashMap;

public class CopyableMap<K, V> extends HashMap<K, V> implements Copyable<CopyableMap<K, V>> {

    @Override
    public CopyableMap<K, V> makeCopy() {
        CopyableMap<K, V> map = new CopyableMap<>();
        map.putAll(this);
        return map;
    }
}
