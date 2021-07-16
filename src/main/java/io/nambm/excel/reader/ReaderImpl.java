package io.nambm.excel.reader;

import io.nambm.excel.Reader;
import io.nambm.excel.model.Raw;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReaderImpl implements Reader {
    @Override
    public <T> List<T> read(InputStream stream, ReaderConfig<T> config) {
        List<Raw<T>> rawList = readRaw(stream, config);
        return convert(rawList);
    }

    @Override
    public <T> List<Raw<T>> readRaw(InputStream stream, ReaderConfig<T> config) {
        BaseReader reader = new BaseReader();
        return reader.readSingleSheet(stream, config);
    }

    @Override
    public <T> Map<String, List<T>> readMultipleSheets(InputStream stream, ReaderConfig<T> config) {
        Map<String, List<Raw<T>>> map = readMultipleSheetsRaw(stream, config);
        Map<String, List<T>> result = new LinkedHashMap<>();
        map.forEach((s, rawList) -> result.put(s, convert(rawList)));
        return result;
    }

    @Override
    public <T> Map<String, List<Raw<T>>> readMultipleSheetsRaw(InputStream stream, ReaderConfig<T> config) {
        BaseReader reader = new BaseReader();
        return reader.readMultipleSheets(stream, config);
    }

    @Override
    public <T> Map<String, List<T>> readAllSheets(InputStream stream, ReaderConfig<T> config) {
        Map<String, List<Raw<T>>> map = readAllSheetsRaw(stream, config);
        Map<String, List<T>> result = new LinkedHashMap<>();
        map.forEach((s, rawList) -> result.put(s, convert(rawList)));
        return result;
    }

    @Override
    public <T> Map<String, List<Raw<T>>> readAllSheetsRaw(InputStream stream, ReaderConfig<T> config) {
        BaseReader reader = new BaseReader();
        return reader.readAllSheets(stream, config);
    }

    private <T> List<T> convert(List<Raw<T>> rawList) {
        return rawList.stream().map(Raw::getData).collect(Collectors.toList());
    }
}
