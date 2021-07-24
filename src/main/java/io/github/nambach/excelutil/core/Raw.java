package io.github.nambach.excelutil.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A wrapper of DTO that contains the resulted DTO after reading process
 * and other properties found while reading the Excel file.
 *
 * @param <T>
 */
@Getter
@Setter
@ToString
public class Raw<T> {
    /**
     * DTO resulted from the reading process
     */
    private T data;

    /**
     * A map that contains other values found while reading a DTO row.
     * These values might have no mapping rules so that it cannot be
     * mapped into the DTO class.
     * <p>
     * Map Key: column title or column index
     * Map Value: cell value of DTO row
     */
    private Map<String, Object> otherData;

    Raw() {
        otherData = new LinkedHashMap<>();
    }
}
