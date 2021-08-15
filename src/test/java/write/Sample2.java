package write;

import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.core.Editor;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import model.Book;
import model.Constant;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;
import java.util.Comparator;

import static java.util.Comparator.comparing;

public class Sample2 {
    static final Style VCENTER = Style.builder().verticalAlignment(VerticalAlignment.CENTER).build();

    static final DataTemplate<Book> BOOK_TEMPLATE = DataTemplate
            .fromClass(Book.class)
            .includeFields("title")
            .column(c -> c.field("subCategory")
                          .style(VCENTER)
                          .mergeOnValue(true))  // merge your rows based on cell value
            .column(c -> c.title("Category")
                          .style(VCENTER)
                          .transform(book -> book.getCategory().getName())
                          .mergeOnId(book -> book.getCategory().getId()))  // merge your rows based on specific value
            .config(cf -> cf.startAtCell("A2")
                            .autoSizeColumns(true));

    public static void main(String[] args) {
        Constant.BOOKS.sort(Comparator.comparing((Book book) -> book.getCategory().getId())
                                      .thenComparing(comparing(Book::getSubCategory).reversed())
                                      .thenComparing(Book::getTitle));
        try (Editor editor = new Editor().configSheet(cf -> cf.debug(true))) {
            editor.writeData(BOOK_TEMPLATE, Constant.BOOKS);
            InputStream stream = editor.exportToFile();
            FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\books.xlsx", stream, true);
        }

    }
}
