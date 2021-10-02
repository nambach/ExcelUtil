package read;

import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.core.RowError;
import io.github.nambach.excelutil.util.FileUtil;
import io.github.nambach.excelutil.validator.FieldError;
import io.github.nambach.excelutil.validator.ObjectError;
import io.github.nambach.excelutil.validator.Validator;
import lombok.SneakyThrows;
import model.Book;

import java.io.InputStream;

public class Sample3 {
    public static final Validator<Book> VALIDATOR = Validator
            .fromClass(Book.class)
            .on(f -> f.field("isbn")
                      .validate(v -> v.isString().notNull()
                                      .lengthBetween(10, 13, "Length must between 10 and 13")))
            .on(f -> f.field("title")
                      .validate(v -> v.isString().notBlank("Title must be provided")))
            .on(f -> f.field("author")
                      .validate(v -> v.isString().notBlank("Author must be provided")))
            .on(f -> f.field("rating")
                      .validate(v -> v.isDecimal().notNull().between(0, 5)));

    static final ReaderConfig<Book> READER_CONFIG = ReaderConfig
            .fromClass(Book.class)
            .titleAtRow(1)
            .dataFromRow(2)
//            .exitWhenValidationFailed(true)
            .column(0, "isbn")
            .column(1, "title")
            .column(2, "author")
            .column(5, "rating")
            .column("First Published", "firstPublished")
            .column("Category", "subCategory")
            .validator(VALIDATOR);


    @SneakyThrows
    public static void main(String[] args) {
        InputStream stream = FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\books.xlsx");
        Result<Book> books = READER_CONFIG.readSheet(stream);

        for (Book book : books) {
            System.out.println(book);
        }

        System.out.println();

        if (books.noError()) {
            System.out.println("No error.");
        }
        for (RowError line : books.getErrors()) {
            int atLine = line.getIndex();
            System.out.println("Line " + (atLine + 1));
            String message = line.getInlineMessage();
            System.out.println(message);

            ObjectError objectError = line.getObjectError();
            for (FieldError field : objectError) {
                String dtoField = field.getFieldName();
//                System.out.println("Field " + dtoField);
//                field.getMessages().forEach(System.out::println);
            }
        }

    }
}
