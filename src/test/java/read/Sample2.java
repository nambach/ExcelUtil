package read;

import io.github.nambach.excelutil.core.LineError;
import io.github.nambach.excelutil.core.Raw;
import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.SneakyThrows;
import model.Book;

import java.io.InputStream;

public class Sample2 {
    static final ReaderConfig<Book> READER_CONFIG = ReaderConfig
            .fromClass(Book.class)
            .titleAtRow(1)
            .dataFromRow(2)
            .exitWhenValidationFailed(true)
            .column(0, "isbn", v -> v.isString().minLength(10, "ISBN must be at least 20 chars"))
            .column(1, "title", v -> v.notNull("Title must not be null"))
            .column(2, "author", v -> v.isString().notBlank("Must provide author"))
            .column(5, "rating", v -> v.isDecimal().notNull())
            .column("First Published", "firstPublished")
            .column("Category", "subCategory")
            .beforeAddingItem((book, row) -> {
                if (book.getRating() < 4) {
                    row.skipThisObject();
                }
            });


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
        for (LineError line : books.getErrors()) {
            System.out.println(line);
//            System.out.println(line.getLine());
//            System.out.println(line.getMessage());
            System.out.println();
        }

    }
}
