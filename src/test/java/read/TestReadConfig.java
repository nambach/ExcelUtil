package read;

import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.SneakyThrows;
import model.Book;
import model.Constant;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class TestReadConfig {

    static final DataTemplate<Book> BOOK_TEMPLATE = DataTemplate
            .fromClass(Book.class)
            .includeFields("isbn", "title", "author", "rating")
            .column(c -> c.title("Category").transform(b -> b.getCategory().getName()))
            .config(cf -> cf.autoSizeColumns(true)
                            .startAtCell("C3"));

    static final DataTemplate<Book> BOOK_TEMPLATE_NO_HEADER = BOOK_TEMPLATE
            .cloneSelf()
            .config(cf -> cf.noHeader(true));

    static final String fileName = "C:\\Users\\Nam Bach\\Desktop\\test-books.xlsx";
    static final String fileNameNoHeader = "C:\\Users\\Nam Bach\\Desktop\\test-books-no-header.xlsx";

    //    @Before
    public void initFile() {
        System.out.println(Thread.currentThread().getName());
        InputStream stream = BOOK_TEMPLATE.writeData(Constant.BOOKS);
        FileUtil.writeToDisk(fileName, stream, true);
    }

    //    @Before
    public void initFileNoHeader() {
        System.out.println(Thread.currentThread().getName());
        InputStream stream = BOOK_TEMPLATE_NO_HEADER.writeData(Constant.BOOKS);
        FileUtil.writeToDisk(fileNameNoHeader, stream, true);
    }

    @Test
    @SneakyThrows
    public void readByTitle() {
        ReaderConfig<Book> config = BOOK_TEMPLATE.getReaderConfig();
        InputStream stream = FileUtil.readFromDisk(fileName);
        Result<Book> books = config.readSheet(stream);
        Assert.assertEquals(Constant.BOOKS.size(), books.size());
        System.out.println(books.size());
        Assert.assertEquals(Constant.BOOKS.get(0).getRating(), books.get(0).getRating(), 0);

        for (Book book : books) {
            Assert.assertThat(book.getIsbn(), IsNull.notNullValue());
            Assert.assertThat(book.getTitle(), IsNull.notNullValue());
            Assert.assertThat(book.getAuthor(), IsNull.notNullValue());
            Assert.assertThat(book.getRating(), IsNot.not(0.0));
            Assert.assertThat(book.getCategory(), IsNull.nullValue());
        }
    }

    @Test
    @SneakyThrows
    public void readByIndex() {
        ReaderConfig<Book> config = BOOK_TEMPLATE.getReaderConfigByColumnIndex();
        InputStream stream = FileUtil.readFromDisk(fileName);
        Result<Book> books = config.readSheet(stream);
        Assert.assertEquals(Constant.BOOKS.size(), books.size());
        System.out.println(books.size());
        Assert.assertEquals(Constant.BOOKS.get(0).getRating(), books.get(0).getRating(), 0);

        for (Book book : books) {
            Assert.assertThat(book.getIsbn(), IsNull.notNullValue());
            Assert.assertThat(book.getTitle(), IsNull.notNullValue());
            Assert.assertThat(book.getAuthor(), IsNull.notNullValue());
            Assert.assertThat(book.getRating(), IsNot.not(0.0));
            Assert.assertThat(book.getCategory(), IsNull.nullValue());
        }
    }

    @Test
    @SneakyThrows
    public void readByIndexNoHeader() {
        ReaderConfig<Book> config = BOOK_TEMPLATE_NO_HEADER.getReaderConfigByColumnIndex();
        InputStream stream = FileUtil.readFromDisk(fileNameNoHeader);
        Result<Book> books = config.readSheet(stream);
        Assert.assertEquals(Constant.BOOKS.size(), books.size());
        System.out.println(books.size());
        Assert.assertEquals(Constant.BOOKS.get(0).getRating(), books.get(0).getRating(), 0);

        for (Book book : books) {
            Assert.assertThat(book.getIsbn(), IsNull.notNullValue());
            Assert.assertThat(book.getTitle(), IsNull.notNullValue());
            Assert.assertThat(book.getAuthor(), IsNull.notNullValue());
            Assert.assertThat(book.getRating(), IsNot.not(0.0));
            Assert.assertThat(book.getCategory(), IsNull.nullValue());
        }
    }
}
