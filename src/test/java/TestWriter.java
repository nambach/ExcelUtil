import io.github.nambach.excelutil.core.DataTemplate;
import io.github.nambach.excelutil.core.SequentialWriter;
import io.github.nambach.excelutil.core.Template;
import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.SneakyThrows;
import model.Address;
import model.Student;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestWriter {

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
            .border(BorderSide.FULL)
            .build();

    public static List<Student> students = Arrays.asList(
            new Student("Olivia", "Anderson", new Address("12", "Wall", "New York"), 8, new Date()),
            new Student("Alison", "Vance", new Address("8A", "Catherine Drive", "Moorhead"), 6.5, new Date()),
            new Student("Olivia", "Anderson", new Address("12", "Wall", "New York"), 8, new Date()),
            new Student("Nicola", "Hart", new Address("2966", "Chestnut Street", "LODI"), 4.5, new Date()),
            new Student("Olivia", "Anderson", new Address("12", "Wall", "New York"), 8, new Date()),
            new Student("Alison", "Vance", new Address("8A", "Catherine Drive", "Moorhead"), 6.5, new Date()),
            new Student("Nicola", "Hart", new Address("2966", "Chestnut Street", "LODI"), 4.5, new Date()),
            new Student("Olivia", "Anderson", new Address("12", "Wall", "New York"), 8, new Date()),
            new Student("Alison", "Vance", new Address("8A", "Catherine Drive", "Moorhead"), 6.5, new Date()),
            new Student("Nicola", "Hart", new Address("2966", "Chestnut Street", "LODI"), 4.5, new Date()),
            new Student("Alison", "Vance", new Address("8A", "Catherine Drive", "Moorhead"), 6.5, new Date()),
            new Student("Nicola", "Hart", new Address("2966", "Chestnut Street", "LODI"), 4.5, new Date())
    );

    public static final Style red = Style.builder().fontColorInHex("#FF0000").build();
    public static final Style green = Style.builder().fontColorInHex("#008000").build();

    public static final Style GRAY_BACKGROUND = Style.builder().backgroundColorInHex("#D3D3D3").build();
    public static final Style YELLOW_BACKGROUND = Style.builder().backgroundColorInHex("#FFFF00").build();

    public static final Style DATE = Style.builder().datePattern("dd/MM/yyyy").build();

    public static final Style FIRST_NAME_STYLE = Style.builder().border(BorderSide.TOP).build();
    public static final Style LAST_NAME_STYLE = Style.builder().border(BorderSide.NONE, "#fbff00").build();
    public static final Style MARK_STYLE = Style.builder()
                                                .border(BorderSide.TOP, "#fbff00", BorderStyle.THICK)
                                                .border(BorderSide.BOTTOM, "#00ff33", BorderStyle.DOUBLE)
                                                .border(BorderSide.RIGHT, "#ff002b", BorderStyle.DOUBLE).build();

    public static final DataTemplate<Student> TEMPLATE_8 = DataTemplate
            .fromClass(Student.class)
            .column(m -> m.field("firstName"))
            .column(m -> m.field("lastName"))
            .column(m -> m.field("mark").title("GPA")
                          .conditionalStyle(s -> s.getMark() < 5 ? red : green))
            .config(config -> config
                    .headerStyle(HEADER_STYLE)
                    .dataStyle(DATA_STYLE)
                    .autoResizeColumns(true)
                    .conditionalRowStyle(s -> s.getMark() > 7 ? YELLOW_BACKGROUND :
                                              s.getMark() < 5 ? GRAY_BACKGROUND : null));

    @SneakyThrows
    public static void main(String[] args) {
        useCase8();
    }

    public static void useCase1() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .cols("firstName", "mark", "date");

        InputStream stream = table.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase1_1() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .cols("firstName", "mark", "date");

        InputStream stream = table.getFileForImport();

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase2() {
        DataTemplate<Student> template = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.title("FName").transform(Student::getFirstName))
                .column(m -> m.title("LName").transform(Student::getLastName))
                .column(m -> m.title("GPA").transform(Student::getMark))
                .column(m -> m.field("date").style(DATE))
                .config(config -> config.autoResizeColumns(true));

        InputStream stream = template.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase2_1() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.field("firstName").title("FName"))
                .column(m -> m.field("lastName").title("LName"))
                .column(m -> m.field("mark").title("GPA"));

        InputStream stream = table.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase3() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.title("Address")
                              .transform(s -> s.getAddress().getCity()))
                .column(m -> m.title("Full Name")
                              .transform(s -> s.getFirstName() + " " + s.getLastName()))
                .column(m -> m.title("GPA (Scale 100)")
                              .transform(s -> s.getMark() * 100))
                .config(config -> config.autoResizeColumns(true));

        InputStream stream = table.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase4() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.field("firstName"))
                .column(m -> m.field("lastName"))
                .column(m -> m.field("mark").title("GPA")
                              .style(GRAY_BACKGROUND));

        InputStream stream = table.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase5() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.field("firstName"))
                .column(m -> m.field("lastName"))
                .column(m -> m.field("mark").title("GPA")
                              .conditionalStyle(s -> s.getMark() < 5 ? red : green))
                .config(cf -> cf.headerStyle(HEADER_STYLE).dataStyle(DATA_STYLE));

        InputStream stream = table.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase6() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.field("firstName"))
                .column(m -> m.field("lastName"))
                .column(m -> m.field("mark").title("GPA"))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true));

        InputStream stream = table.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase7() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.field("firstName")
                              .style(FIRST_NAME_STYLE))
                .column(m -> m.field("lastName")
                              .style(LAST_NAME_STYLE))
                .column(m -> m.field("mark")
                              .style(MARK_STYLE)
                              .title("GPA"))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true));

        InputStream stream = table.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);

    }

    public static void useCase8() {
        InputStream stream = TEMPLATE_8.writeData(students);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    /**
     * multiple sheets
     */
    public static void useCase9() {
        DataTemplate<Student> table = DataTemplate
                .fromClass(Student.class)
                .column(m -> m.field("firstName"))
                .column(m -> m.field("lastName"))
                .column(m -> m.field("mark").title("GPA")
                              .conditionalStyle(s -> s.getMark() < 5 ? red : green))
                .config(config -> config
                        .headerStyle(HEADER_STYLE)
                        .dataStyle(DATA_STYLE)
                        .autoResizeColumns(true)
                        .conditionalRowStyle(s -> s.getMark() > 7 ? YELLOW_BACKGROUND :
                                                  s.getMark() < 5 ? GRAY_BACKGROUND : null));

        SequentialWriter writer = new SequentialWriter();
        writer.createNewSheet("A");
        writer.writeData(table, students);
        writer.createNewSheet("B");
        writer.writeData(table, students);
        InputStream stream = writer.exportToFile();

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCaseTakt2() {
        Style title = Style
                .builder()
                .backgroundColorInHex("#9bc2e6")
                .bold(true).border(BorderSide.FULL).build();
        Style center = Style
                .builder(title)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER)
                .build();
        Style fontRed = Style
                .builder(center)
                .fontColorInHex("#FF0000")
                .build();

        Template template = Template
                .builder()
                .useStyle(title)
                .at("A1").cell(c -> c.text("Factory"))
                .down(c -> c.text("Line"))
                .down(c -> c.text("Station"))
                .at("D1").cell(c -> c.text("From Date"))
                .down(c -> c.text("To Date"))
                .useStyle(center)
                .at("A5").cell(c -> c.text("DAY").rowSpan(2));

        String[] headers = new String[]{"DAY", "SHIFT", "TAKT", "Cycle Time", "Demand /Actual", "Takt attainment", "Takt time"};
        for (int i = 1; i < headers.length; i++) {
            int colAt = i;
            template.right(c -> c.text(headers[colAt]).rowSpan(2));
        }

        template.at("H5").cell(c -> c.text("eAndon").colSpan(3))
                .right(c -> c.date(new Date()))
                .at("H5")
                .down(c -> c.text("Type"))
                .right(c -> c.text("Detail"))
                .right(c -> c.text("Alert Duration [M]").style(fontRed));

        SequentialWriter writer = new SequentialWriter();
        writer.createNewSheet("Sheet 1");
        writer.writeTemplate(template);
        writer.writeLine(0, c -> c.text("ABC"));
        writer.freeze(6, 0);
        InputStream stream = writer.exportToFile();

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }
}