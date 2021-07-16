import io.nambm.excel.SimpleWriter;
import io.nambm.excel.writer.SimpleWriterImpl;
import io.nambm.excel.writer.Table;
import io.nambm.excel.style.BorderSide;
import io.nambm.excel.style.Style;
import io.nambm.excel.util.FileUtil;
import lombok.SneakyThrows;
import model.Transportation;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;

public class TestMerge {

    public static final Style HEADER_STYLE = Style
            .builder()
            .fontName("Calibri")
            .fontSize((short) 12)
            .bold(true)
            .fontColorInHex("#ffffff")          // white
            .backgroundColorInHex("#008000")    // green
            .border(BorderSide.FULL)
            .horizontalAlignment(HorizontalAlignment.LEFT)
            .build();

    public static final Style DATA_STYLE = Style
            .builder()
            .fontName("Times New Roman")
            .verticalAlignment(VerticalAlignment.CENTER)
            .border(BorderSide.FULL)
            .build();

    public static final Style red = Style.builder().fontColorInHex("#FF0000").build();
    public static final Style green = Style.builder().fontColorInHex("#008000").build();

    public static final Style GRAY_BACKGROUND = Style.builder().backgroundColorInHex("#D3D3D3").build();
    public static final Style YELLOW_BACKGROUND = Style.builder().backgroundColorInHex("#FFFF00").build();

    @SneakyThrows
    public static void main(String[] args) {
        useCase8();
    }

    public static void useCase8() {
        Table<Transportation> table = Table
                .fromClass(Transportation.class)
                .col(m -> m.field("name"))
                .col(m -> m.field("quantity"))
                .col(m -> m.field("brand")
                           .mergeOnValue(true))
                .col(m -> m.title("Type")
                           .transform(t -> t.getCategory().getName())
                           .mergeOnId(t -> t.getCategory().getId()))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true));

        SimpleWriter exporter = new SimpleWriterImpl();
        InputStream stream = exporter.write(Transportation.SAMPLE, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }
}