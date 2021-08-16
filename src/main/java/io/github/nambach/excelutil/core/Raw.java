package io.github.nambach.excelutil.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A wrapper of DTO that contains the resulted DTO after reading process
 * and other properties found while reading the Excel file.
 *
 * @param <T> DTO
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@ToString
public class Raw<T> {
    /**
     * A map that contains other values found while reading a DTO row.
     * These values might have no mapping rules so that it cannot be
     * mapped into the DTO class.
     * <p>
     * Map Key: column title or column index
     * Map Value: cell value of DTO row
     */
    private final Map<String, Object> otherData;
    /**
     * DTO resulted from the reading process.
     */
    private T data;

    Raw() {
        otherData = new LinkedHashMap<>();
    }
}
