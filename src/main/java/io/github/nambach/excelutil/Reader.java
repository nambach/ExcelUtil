package io.github.nambach.excelutil;

import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.model.Raw;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Reader {
    <T> List<T> read(InputStream stream, ReaderConfig<T> config);

    <T> List<Raw<T>> readRaw(InputStream stream, ReaderConfig<T> config);

    <T> Map<String, List<T>> readMultipleSheets(InputStream stream, ReaderConfig<T> config);

    <T> Map<String, List<Raw<T>>> readMultipleSheetsRaw(InputStream stream, ReaderConfig<T> config);

    <T> Map<String, List<T>> readAllSheets(InputStream stream, ReaderConfig<T> config);

    <T> Map<String, List<Raw<T>>> readAllSheetsRaw(InputStream stream, ReaderConfig<T> config);
}