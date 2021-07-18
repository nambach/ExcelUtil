import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import io.github.nambach.excelutil.core.DataTemplate;
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
        DataTemplate<Transportation> table = DataTemplate
                .fromClass(Transportation.class)
                .column(m -> m.field("name"))
                .column(m -> m.field("quantity"))
                .column(m -> m.field("brand")
                              .mergeOnValue(true))
                .column(m -> m.title("Type")
                              .transform(t -> t.getCategory().getName())
                              .mergeOnId(t -> t.getCategory().getId()))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true));

        InputStream stream = table.writeData(Transportation.SAMPLE);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }
}