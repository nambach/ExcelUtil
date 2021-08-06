package read;

import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.SneakyThrows;
import model.Book;

import java.io.InputStream;

public class Sample1 {
    static final ReaderConfig<Book> READER_CONFIG = ReaderConfig
            .fromClass(Book.class)
            .titleAtRow(0)
            .dataFromRow(1)
            .column(0, "ibsn")
            .column(1, "title")
            .handler(set -> set.atColumn(2)
                               .handle((book, cell) -> {
                                   String value = cell.readString();
                                   book.getCategory().setName(value);
                               }));


    @SneakyThrows
    public static void main(String[] args) {
        InputStream stream = FileUtil.readFromDisk(".../book.xlsx");
        Result<Book> books = READER_CONFIG.readSheet(stream);
    }
}
