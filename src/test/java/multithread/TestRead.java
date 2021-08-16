package multithread;

import com.google.code.tempusfugit.concurrency.ConcurrentTestRunner;
import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.SneakyThrows;
import model.Book;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

@RunWith(ConcurrentTestRunner.class)
public class TestRead {
    static final ReaderConfig<Book> CONFIG = TestWrite.BOOK_TEMPLATE
            .getReaderConfig()
            .handler(set -> set.atColumn("Category")
                               .handle((book, cell) -> book.setSubCategory(cell.readString())));

    @Test
    @SneakyThrows
    public void testFiction() {
        InputStream stream = FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\fiction.xlsx");
        Result<Book> fictions = CONFIG.readSheet(stream);
        int count = (int) fictions.stream().filter(book -> book.getSubCategory().equals("Fiction")).count();
        System.out.println(count);
        Assert.assertEquals(Data.BOOKS_1.size(), count);
    }

    @Test
    @SneakyThrows
    public void testNonFiction() {
        InputStream stream = FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\nonFiction.xlsx");
        Result<Book> nonFictions = CONFIG.readSheet(stream);
        int count = (int) nonFictions.stream().filter(book -> book.getSubCategory().equals("Non-fiction")).count();
        System.out.println(count);
        Assert.assertEquals(Data.BOOKS_2.size(), count);
    }

    @Test
    @SneakyThrows
    public void testScience() {
        InputStream stream = FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\science.xlsx");
        Result<Book> science = CONFIG.readSheet(stream);
        int count = (int) science.stream().filter(book -> book.getSubCategory().equals("Science")).count();
        System.out.println(count);
        Assert.assertEquals(Data.BOOKS_3.size(), count);
    }
}
