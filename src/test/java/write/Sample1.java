package write;

import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import model.Book;
import model.Constant;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.InputStream;

public class Sample1 {

    // It is recommended to pre-define your styles in a file, since
    // Apache POI limits the total number of styles in one workbook
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
            .column(c -> c.field("isbn").title("ISBN"))  // customize column title
            .includeFields("title", "author")
            .column(c -> c.title("Category")
                          .transform(book -> book.getCategory().getName()))  // derive new column
            .column(c -> c.field("rating")
                          .conditionalStyle(book -> book.getRating() > 4f ?  // styles with conditions
                                                    HIGH_RATE : null))
            .config(cf -> cf.startAtCell("A2")  // some sorts of configuration
                            .autoSizeColumns(true)
                            .headerStyle(HEADER_STYLE)
                            .dataStyle(DATA_STYLE)
                            .conditionalRowStyle(book -> book.getTitle().contains("Harry Potter") ? FAVORITE_ONE : null));

    public static void main(String[] args) {
        InputStream stream = BOOK_TEMPLATE.writeData(Constant.BOOKS);

        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\books.xlsx", stream, true);
    }
}
