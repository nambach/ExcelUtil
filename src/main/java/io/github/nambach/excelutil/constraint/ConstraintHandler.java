package io.github.nambach.excelutil.constraint;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.github.nambach.excelutil.constraint.ConstraintProperty.Dropdown;

public class ConstraintHandler {
    private final Workbook workbook;
    private final Map<String, DataValidation> cache = new HashMap<>();

    public ConstraintHandler(Workbook workbook) {
        this.workbook = workbook;
    }

    private String computeKey(Constraint constraint, Sheet sheet) {
        return String.format("%s:%s", workbook.getSheetIndex(sheet), constraint.getUuid());
    }

    private DataValidation getCache(Constraint constraint, Sheet sheet) {
        return cache.get(computeKey(constraint, sheet));
    }

    private void putCache(Constraint constraint, Sheet sheet, DataValidation validation) {
        cache.put(computeKey(constraint, sheet), validation);
    }

    public void applyConstraint(Constraint constraint, Cell cell) {
        if (constraint == null) {
            return;
        }

        Sheet sheet = cell.getSheet();

        DataValidation validation = getCache(constraint, sheet);
        if (validation != null) {
            validation.getRegions().addCellRangeAddress(cell.getRowIndex(), cell.getColumnIndex(),
                                                        cell.getRowIndex(), cell.getColumnIndex());
            return;
        }


        constraint.getProperty(Dropdown).getAny(ArrayList.class).ifPresent(values -> applyDropdown(values, constraint, cell));

    }

    private void applyDropdown(Collection<?> values, Constraint constraint, Cell cell) {
        Sheet sheet = cell.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();

        String[] strings = values.stream().map(Object::toString).toArray(String[]::new);
        DataValidationConstraint dvConstraint = helper.createExplicitListConstraint(strings);

        CellRangeAddressList addressList = new CellRangeAddressList(cell.getRowIndex(), cell.getRowIndex(),
                                                                    cell.getColumnIndex(), cell.getColumnIndex());
        DataValidation validation = helper.createValidation(dvConstraint, addressList);
        // Note the check on the actual type of the DataValidation object.
        // If it is an instance of the XSSFDataValidation class then the
        // boolean value 'false' must be passed to the setSuppressDropDownArrow()
        // method and an explicit call made to the setShowErrorBox() method.
        if (validation instanceof XSSFDataValidation) {
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
        } else {
            // If the DataValidation contains an instance of the HSSFDataValidation
            // class then 'true' should be passed to the setSuppressDropDownArrow()
            // method and the call to setShowErrorBox() is not necessary.
            validation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(validation);

        // save cache
        putCache(constraint, sheet, validation);
    }
}
