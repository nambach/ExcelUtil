package io.github.nambach.excelutil.core;

import org.apache.poi.ss.util.CellAddress;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

interface Navigation<T extends Navigation<T>> {

    default Collection<CellAddress> parseAddress(Collection<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return Collections.emptyList();
        }
        TreeSet<CellAddress> set = new TreeSet<>(CellAddress::compareTo);
        for (String address : addresses) {
            if (address.contains(":")) {
                String[] split = address.split(":");
                CellAddress from = new CellAddress(split[0].trim());
                CellAddress to = new CellAddress(split[1].trim());
                for (int rowNo = from.getRow(); rowNo <= to.getRow(); rowNo++) {
                    for (int colNo = from.getColumn(); colNo <= to.getColumn(); colNo++) {
                        set.add(new CellAddress(rowNo, colNo));
                    }
                }
            } else {
                set.add(new CellAddress(address.trim()));
            }
        }
        return set;
    }

    T goToCell(String address);

    T goToCell(int row, int col);

    T next();

    T next(int steps);

    T down();

    T down(int steps);

    T enter();

    T enter(int steps);
}
