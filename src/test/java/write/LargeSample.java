package write;

import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.core.Editor;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import model.Book;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jeasy.random.EasyRandom;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.List;
import java.util.stream.Collectors;

import static write.Sample1.BOOK_TEMPLATE;

public class LargeSample {

    @SneakyThrows
    public static void main(String[] args) {
        org.openjdk.jmh.Main.main(args);
        new LargeSample().test();
    }

    public void test() {
        Data data = new Data();
        data.setup();
        testLargeXlsx(data);
        testXlsx(data);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1)
    @Warmup(iterations = 2, time = 5)
    @Measurement(iterations = 2, time = 5)
    public void testXlsx(Data data) {
        DataTemplate<Book> template = BOOK_TEMPLATE.makeCopy().config(cf -> cf.autoSizeColumns(false));
        @Cleanup Editor editor = new Editor();
        editor.writeData(template, data.books);
        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\many-books.xlsx", editor.exportToFile(), true);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1)
    @Warmup(iterations = 2, time = 5)
    @Measurement(iterations = 2, time = 5)
    @SneakyThrows
    public void testLargeXlsx(Data data) {
        DataTemplate<Book> template = BOOK_TEMPLATE.makeCopy().config(cf -> cf.autoSizeColumns(false));
        @Cleanup SXSSFWorkbook workbook = new SXSSFWorkbook();
        @Cleanup Editor editor = new Editor(workbook);
        editor.writeData(template, data.books);
        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\many-books-2.xlsx", editor.exportToFile(), true);
    }

    @State(Scope.Benchmark)
    public static class Data {
        List<Book> books;

        @Setup(Level.Invocation)
        public void setup() {
            books = new EasyRandom().objects(Book.class, 10_000).collect(Collectors.toList());
        }
    }
}
