package io.nambm.excel;

import io.nambm.excel.writer.Table;

import java.io.ByteArrayInputStream;
import java.util.Collection;

public interface SimpleWriter {
    <T> ByteArrayInputStream write(Collection<T> data,
                                   Table<T> table);

    <T> ByteArrayInputStream writeTemplate(Table<T> table);
}
