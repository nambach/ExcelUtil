package io.github.nambach.excelutil.util;

public interface Copyable<T extends Copyable<T>> {
    T makeCopy();
}
