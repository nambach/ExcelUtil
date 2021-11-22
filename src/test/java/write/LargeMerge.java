package write;

import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import model.Book;
import model.Book.Category;
import model.LargeConstant;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;
import java.util.Collection;

public class LargeMerge {
    static final Style VCENTER = Style.builder()
                                      .fontName("Times New Roman").border(BorderSide.FULL)
                                      .verticalAlignment(VerticalAlignment.CENTER).build();

    static final DataTemplate<Category> CATEGORY_TEMPLATE = DataTemplate
            .fromClass(Category.class)
            .includeFields("name")
            .column(c -> c.field("books")
                          .expandRows(Book.class, bookCols -> bookCols
                                  .includeFields("isbn", "title", "firstPublished", "author", "subCategory")
                                  .column(bc -> bc.field("chars").expandRows())))
            .config(cf -> cf.startAtCell("A2")
                            .autoSizeColumns(true)
                            .dataStyle(VCENTER));

    public static void main(String[] args) {
        Collection<Category> all = LargeConstant.CAT.values();
        System.out.println("Starting...");
        long start = System.currentTimeMillis();
        InputStream stream = CATEGORY_TEMPLATE.writeData(all);
        long total = System.currentTimeMillis() - start;
        System.out.println(total / 1000);

        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\reverse-books.xlsx", stream, true);
    }
}
