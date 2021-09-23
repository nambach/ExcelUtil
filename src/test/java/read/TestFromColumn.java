package read;

import io.github.nambach.excelutil.core.Editor;
import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

public class TestFromColumn {
    @SneakyThrows
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {
        List<LinkedHashMap> result;
        ReaderConfig<LinkedHashMap> config = ReaderConfig
                .fromClass(LinkedHashMap.class)
                .titleAtRow(0)
                .dataFromRow(1)
                .handler(set -> set.fromColumn(0)
                                   .handle((linkedHashMap, readerCell) -> {
                                       String title = readerCell.getColumnTitle();
                                       String value = readerCell.readString();
                                       linkedHashMap.put(title, value);
                                   }));
        InputStream stream = FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\Test-Inter-CF.xlsx");
        Editor editor = new Editor(stream);
        result = editor.goToCell("C6").readSection(config);

        for (LinkedHashMap linkedHashMap : result) {
            System.out.println(linkedHashMap);
        }
        result = config.readSheet(FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\Test-Inter-CF2.xlsx"));
    }
}
