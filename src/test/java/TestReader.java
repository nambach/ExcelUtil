import io.nambm.excel.Reader;
import io.nambm.excel.reader.ReaderConfig;
import io.nambm.excel.reader.ReaderImpl;
import io.nambm.excel.util.FileUtil;
import model.Student;

import java.io.FileNotFoundException;
import java.util.List;

public class TestReader {
    public static void main(String[] args) throws FileNotFoundException {
        ReaderConfig<Student> config = TestWriter.TEMPLATE_8
                .getReaderConfig();

        Reader reader = new ReaderImpl();
        List<Student> students = reader.read(FileUtil.readFromDisk("src/main/resources/basic-example.xlsx"), config);
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
