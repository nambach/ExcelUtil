package validator;

import io.github.nambach.excelutil.validator.builtin.TypeValidator;

public class NumericValidator {
    public static void main(String[] args) {
        TypeValidator intValidator = TypeValidator.init().notNull().isInteger();
        TypeValidator decimalValidator = TypeValidator.decimal().between(0.4, 10.9);
        Object val = "10";
        System.out.println(intValidator.test(val));
        System.out.println(decimalValidator.test(val));
    }
}
