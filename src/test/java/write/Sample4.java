package write;

import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import model.Book;
import model.Constant;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;

public class Sample4 {
    static final Style VCENTER = Style.builder().verticalAlignment(VerticalAlignment.CENTER).build();

    static final DataTemplate<Book.Category> TEMPLATE = DataTemplate
            .fromClass(Book.Category.class)
            .includeFields("name")
            .column(c -> c.field("books")
                          .style(VCENTER)
                          .asList(Book.class, "isbn", "title"))
            .config(cf -> cf.startAtCell("A2")
                            .autoSizeColumns(true));

    public static void main(String[] args) {
        TEMPLATE.split(null);
        InputStream stream = TEMPLATE.writeData(Constant.CAT.values());

        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\books.xlsx", stream, true);
    }
}
