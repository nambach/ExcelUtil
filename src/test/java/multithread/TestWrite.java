package multithread;

import com.google.code.tempusfugit.concurrency.ConcurrentTestRunner;
import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import model.Book;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

@RunWith(ConcurrentTestRunner.class)
public class TestWrite {

    static final Style HEADER_STYLE = Style
            .builder()
            .fontName("Calibri").fontSize((short) 12)
            .fontColorInHex("#ffffff")  // white
            .backgroundColorInHex("#191970")  // midnight blue
            .border(BorderSide.FULL)
            .horizontalAlignment(HorizontalAlignment.LEFT)
            .build();

    static final Style DATA_STYLE = Style
            .builder()
            .fontName("Calibri")
            .fontSize((short) 11)
            .border(BorderSide.FULL)
            .build();

    static final Style HIGH_RATE = Style
            .builder().bold(true)
            .fontColorInHex("#008000")  // green
            .build();

    static final Style FAVORITE_ONE = Style.builder().backgroundColorInHex("#FFB6C1").build();

    static final DataTemplate<Book> BOOK_TEMPLATE = DataTemplate
            .fromClass(Book.class)
            .includeFields("isbn", "title", "author")
            .column(c -> c.field("rating").conditionalStyle(b -> (b.getRating() >= 4) ? HIGH_RATE : null))
            .column(c -> c.title("Category").transform(b -> b.getCategory().getName()))
            .config(cf -> cf.headerStyle(HEADER_STYLE)
                            .dataStyle(DATA_STYLE)
                            .autoSizeColumns(true)
                            .conditionalRowStyle(book -> book.getTitle().startsWith("H") ? FAVORITE_ONE : null));

    @Test
    public void fiction() {
        System.out.println(Thread.currentThread().getName());
        InputStream stream = BOOK_TEMPLATE.writeData(Data.BOOKS_1);
        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\fiction.xlsx", stream, true);
    }

    @Test
    public void science() {
        System.out.println(Thread.currentThread().getName());
        InputStream stream = BOOK_TEMPLATE.writeData(Data.BOOKS_3);
        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\science.xlsx", stream, true);
    }

    @Test
    public void nonFiction() {
        System.out.println(Thread.currentThread().getName());
        InputStream stream = BOOK_TEMPLATE.writeData(Data.BOOKS_2);
        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\nonFiction.xlsx", stream, true);
    }


}
