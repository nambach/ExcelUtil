package io.github.nambach.excelutil.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Raw<T> {
    private T data;
    private Map<String, Object> otherData;

    public Raw() {
        otherData = new LinkedHashMap<>();
    }
}
