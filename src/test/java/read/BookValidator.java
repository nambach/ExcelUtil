package read;

import io.github.nambach.excelutil.validator.Validator;
import model.Book;

public class BookValidator {
    public static final Validator<Book> VALIDATOR = Validator
            .fromClass(Book.class)
            .on(f -> f.field("isbn")
                      .validate(v -> v.isString().notNull().lengthBetween(10, 13)))
            .on(f -> f.field("title")
                      .validate(v -> v.isString().notBlank()))
            .on(f -> f.field("author")
                      .validate(v -> v.isString().notBlank()))
            .on(f -> f.field("rating")
                      .validate(v -> v.isDecimal().notNull()));
}
