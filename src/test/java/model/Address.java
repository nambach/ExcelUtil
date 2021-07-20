package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Address implements Comparable<Address> {
    private String code;
    private String street;
    private String city;

    @Override
    public int compareTo(Address o) {
        return code.compareTo(o.code);
    }
}
