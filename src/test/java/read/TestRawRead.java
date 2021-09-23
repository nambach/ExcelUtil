package read;

import io.github.nambach.excelutil.core.Raw;
import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.Result;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.SneakyThrows;

import java.io.InputStream;

public class TestRawRead {
    @SneakyThrows
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {
        Result<Object> result;
        ReaderConfig<Object> config = ReaderConfig
                .fromClass(Object.class)
                .titleAtRow(0)
                .dataFromRow(1);
        InputStream stream = FileUtil.readFromDisk("C:\\Users\\Nam Bach\\Desktop\\Test-Inter-CF.xlsx");
        result = config.readSheet(stream);
        for (Raw<Object> rawDatum : result.getRawData()) {
            System.out.println(rawDatum.getOtherData());
        }
    }
}
