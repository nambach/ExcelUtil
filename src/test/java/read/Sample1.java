package read;

import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.ReaderError;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.util.FileUtil;
import io.github.nambach.excelutil.validator.builtin.Validator;
import lombok.SneakyThrows;
import model.Book;

import java.io.InputStream;

public class Sample1 {
    static final ReaderConfig<Book> READER_CONFIG = ReaderConfig
            .fromClass(Book.class)
            .titleAtRow(1)
            .dataFromRow(2)
            .column(0, "isbn", Validator.string())
            .column(1, "title")
            .handler(set -> set.atColumn(2)
                               .handle((book, cell) -> {
                                   String value = cell.readString();
                                   book.setAuthor(value);
                                   if (value.equals("Marijn Haverbeke")) {
//                                       cell.setError("JS detected");
                                   }
                               }));


    @SneakyThrows
    public static void main(String[] args) {
        InputStream stream = FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\books.xlsx");
        Result<Book> books = READER_CONFIG.readSheet(stream);

        for (Book book : books) {
            System.out.println(book);
        }

        System.out.println();

        if (!books.hasError()) {
            System.out.println("No error.");
        }
        for (ReaderError.Line line : books.getError()) {
            System.out.println(line);
        }

    }
}
