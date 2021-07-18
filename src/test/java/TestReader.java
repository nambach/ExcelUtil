import io.github.nambach.excelutil.Reader;
import io.github.nambach.excelutil.core.Editor;
import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.core.ReaderImpl;
import io.github.nambach.excelutil.util.FileUtil;
import model.Student;

import java.io.FileNotFoundException;
import java.util.List;

public class TestReader {
    public static void main(String[] args) throws FileNotFoundException {
        Editor editor = new Editor(FileUtil.readFromDisk("src/main/resources/basic-example.xlsx"));
        System.out.println(editor.readString());
        System.out.println(editor.goToCell("K5").readDate());
    }

    private static void useCase1() throws FileNotFoundException {
        ReaderConfig<Student> config = TestWriter.TEMPLATE_8
                .getReaderConfig();

        Reader reader = new ReaderImpl();
        List<Student> students = reader.read(FileUtil.readFromDisk("src/main/resources/basic-example.xlsx"), config);
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
