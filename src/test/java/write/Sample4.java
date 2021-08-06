package write;

import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import model.Book;
import model.Constant;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;

public class Sample4 {
    static final Style VCENTER = Style.builder()
                                      .fontName("Times New Roman").border(BorderSide.FULL)
                                      .verticalAlignment(VerticalAlignment.CENTER).build();

    static final DataTemplate<Book.Category> CATEGORY_TEMPLATE = DataTemplate
            .fromClass(Book.Category.class)
            .includeFields("name")
            .column(c -> c.field("books")
                          .asList(Book.class, bookCols -> bookCols
                                  .includeFields("isbn", "title")
                                  .column(bc -> bc.field("chars").asList())))
            .config(cf -> cf.startAtCell("A2")
                            .autoSizeColumns(true)
                            .dataStyle(VCENTER));

    public static void main(String[] args) {
        InputStream stream = CATEGORY_TEMPLATE.writeData(Constant.CAT.values());

        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\reverse-books.xlsx", stream, true);
    }
}
