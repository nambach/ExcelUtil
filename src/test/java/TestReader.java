import io.nambm.excel.Reader;
import io.nambm.excel.reader.ReaderConfig;
import io.nambm.excel.reader.ReaderImpl;
import io.nambm.excel.util.FileUtil;
import model.Student;

import java.io.FileNotFoundException;
import java.util.List;

public class TestReader {
    public static void main(String[] args) throws FileNotFoundException {
        ReaderConfig<Student> config = ReaderConfig
                .fromClass(Student.class)
                .rowDataFrom(1)
                .handlers(builder -> builder
                        .byColIndex(1, (Student s, String v) -> {
                            String[] arr = v.split(" ");
                            s.setFirstName(arr[0]);
                            s.setLastName(arr[1]);
                        })
                        .byColIndex(2, (Student s, Double v) -> {
                            s.setMark(v / 100);
                        }));

        Reader reader = new ReaderImpl();
        List<Student> students = reader.read(FileUtil.readFromDisk("src/main/resources/basic-example.xlsx"), config);
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
