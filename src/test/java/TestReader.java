import io.github.nambach.excelutil.core.Editor;
import io.github.nambach.excelutil.core.ReaderConfig;
import io.github.nambach.excelutil.util.FileUtil;
import model.Address;
import model.Student;

import java.io.FileNotFoundException;
import java.util.List;

public class TestReader {
    public static void main(String[] args) throws FileNotFoundException {
        useCase1();
    }

    private static void useCaseTakt() throws FileNotFoundException {
        Editor editor = new Editor(FileUtil.readFromDisk("src/main/resources/basic-example.xlsx"));
        System.out.println(editor.readString());
        System.out.println(editor.goToCell("K5").readDate());
    }

    private static void useCase1() throws FileNotFoundException {
        ReaderConfig<Student> config = TestWriter.TEMPLATE_8
                .getReaderConfig()
                .handler(h -> h.atColumn(0).handle((student, cell) -> {
                    String v = cell.readString();
                    if ("Nicola".equals(v)) {
                        Address a = new Address(cell.getAddress(), null, null);
                        student.setAddress(a);
                    }
                }));

        List<Student> students = config.readSheet(FileUtil.readFromDisk("src/main/resources/basic-example.xlsx"), 0);
        int i = 1;
        for (Student student : students) {
            System.out.print(i++ + " ");
            System.out.println(student);
        }
    }
}
