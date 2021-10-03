package read;

import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.core.RowError;
import io.github.nambach.excelutil.util.FileUtil;
import io.github.nambach.excelutil.validator.FieldError;
import io.github.nambach.excelutil.validator.ObjectError;
import lombok.SneakyThrows;
import model.Book;

import java.io.InputStream;
import java.util.List;

public class Sample2 {
    static final ReaderConfig<Book> READER_CONFIG = ReaderConfig
            .fromClass(Book.class)
            .titleAtRow(1)
            .dataFromRow(2)
//            .exitWhenValidationFailed(true)
            .column(0, "isbn", v -> v.isString().lengthBetween(10, 13, "Length must between 10 and 13"))
            .column(1, "title", v -> v.notNull("Title must not be null"))
            .column(2, "author", v -> v.isString().notBlank("Must provide author"))
            .column(3, "rating", v -> v.isDecimal().notNull().between(0, 5, "Rating must be between 0 and 5"))
            .column("First Published", "firstPublished")
            .column("Category", "subCategory");


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
        for (RowError row : books.getErrors()) {
            int atRow = row.getExcelIndex();
            String message = row.getInlineMessage();
            System.out.println("Row " + atRow + ": " + message);

            ObjectError objectError = row.getObjectError();
            for (FieldError field : objectError) {
                String dtoField = field.getFieldName();
                List<String> dtoFieldErrors = field.getMessages();
//                System.out.println("Field " + dtoField);
//                field.getMessages().forEach(System.out::println);
            }
        }

    }
}
