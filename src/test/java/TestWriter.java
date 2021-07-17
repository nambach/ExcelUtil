import io.nambm.excel.SimpleWriter;
import io.nambm.excel.Writer;
import io.nambm.excel.style.BorderSide;
import io.nambm.excel.style.Style;
import io.nambm.excel.util.FileUtil;
import io.nambm.excel.writer.DeclarativeWriter;
import io.nambm.excel.writer.ImperativeWriter;
import io.nambm.excel.writer.Table;
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
    public static final Style LAST_NAME_STYLE = Style.builder().border(BorderSide.HORIZONTAL, "#fbff00").build();
    public static final Style MARK_STYLE = Style.builder()
                                                .border(BorderSide.TOP, "#fbff00", BorderStyle.THICK)
                                                .border(BorderSide.BOTTOM, "#00ff33", BorderStyle.DOUBLE)
                                                .border(BorderSide.RIGHT, "#ff002b", BorderStyle.DOUBLE).build();

    @SneakyThrows
    public static void main(String[] args) {
        useCase8();
    }

    public static void useCase1() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .cols("firstName", "mark", "date");

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase1_1() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .cols("firstName", "mark", "date");

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.writeTemplate(table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase2() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.title("FName").transform(Student::getFirstName))
                .col(m -> m.title("LName").transform(Student::getLastName))
                .col(m -> m.title("GPA").transform(Student::getMark))
                .col(m -> m.field("date").style(DATE))
                .config(config -> config.autoResizeColumns(true));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase2_1() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.field("firstName").title("FName"))
                .col(m -> m.field("lastName").title("LName"))
                .col(m -> m.field("mark").title("GPA"));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase3() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.title("Address")
                           .transform(s -> s.getAddress().getCity()))
                .col(m -> m.title("Full Name")
                           .transform(s -> s.getFirstName() + " " + s.getLastName()))
                .col(m -> m.title("GPA (Scale 100)")
                           .transform(s -> s.getMark() * 100))
                .config(config -> config.autoResizeColumns(true));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase4() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.field("firstName"))
                .col(m -> m.field("lastName"))
                .col(m -> m.field("mark").title("GPA")
                           .style(GRAY_BACKGROUND));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase5() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.field("firstName"))
                .col(m -> m.field("lastName"))
                .col(m -> m.field("mark").title("GPA")
                           .conditionalStyle(s -> s.getMark() < 5 ? red : green))
                .config(cf -> cf.headerStyle(HEADER_STYLE).dataStyle(DATA_STYLE));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase6() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.field("firstName"))
                .col(m -> m.field("lastName"))
                .col(m -> m.field("mark").title("GPA"))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCase7() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.field("firstName")
                           .style(FIRST_NAME_STYLE))
                .col(m -> m.field("lastName")
                           .style(LAST_NAME_STYLE))
                .col(m -> m.field("mark")
                           .style(MARK_STYLE)
                           .title("GPA"))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);

    }

    public static void useCase8() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.field("firstName"))
                .col(m -> m.field("lastName"))
                .col(m -> m.field("mark").title("GPA")
                           .conditionalStyle(s -> s.getMark() < 5 ? red : green))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true)
                                        .conditionalRowStyle(s -> s.getMark() > 7 ? YELLOW_BACKGROUND :
                                                                  s.getMark() < 5 ? GRAY_BACKGROUND :
                                                                  null));

        SimpleWriter exporter = new DeclarativeWriter();
        InputStream stream = exporter.write(students, table);

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    /**
     * multiple sheets
     */
    public static void useCase9() {
        Table<Student> table = Table
                .fromClass(Student.class)
                .col(m -> m.field("firstName"))
                .col(m -> m.field("lastName"))
                .col(m -> m.field("mark").title("GPA")
                           .conditionalStyle(s -> s.getMark() < 5 ? red : green))
                .config(config -> config.headerStyle(HEADER_STYLE)
                                        .dataStyle(DATA_STYLE)
                                        .autoResizeColumns(true)
                                        .reuseForImport(true)
                                        .conditionalRowStyle(s -> s.getMark() > 7 ? YELLOW_BACKGROUND :
                                                                  s.getMark() < 5 ? GRAY_BACKGROUND : null));

        Writer writer = new ImperativeWriter();
        writer.createNewSheet("A");
        writer.writeData(students, table);
        writer.createNewSheet("B");
        writer.writeData(students, table);
        InputStream stream = writer.exportToFile();

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }

    public static void useCaseTakt() {
        Style title = Style
                .builder()
                .backgroundColorInHex("#9bc2e6")
                .bold(true).border(BorderSide.FULL).build();
        Style center = Style
                .builder()
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER)
                .build();
        Style fontRed = Style.builder().fontColorInHex("#FF0000").build();
        Writer writer = new ImperativeWriter();
        writer.createNewSheet("Sheet 1");
        writer.setCurrentStyle(title);
        writer.writeAnywhere("Factory", 0, 0);
        writer.writeAnywhere("Line", 1, 0);
        writer.writeAnywhere("Station", 2, 0);
        writer.writeAnywhere("From Date", 0, 3);
        writer.writeAnywhere("To Date", 1, 3);
        writer.setCurrentStyle(title, center);
        String[] headers = new String[]{"DAY", "SHIFT", "TAKT", "Cycle Time", "Demand /Actual", "Takt attainment", "Takt time"};
        for (int i = 0; i < headers.length; i++) {
            writer.writeAnywhere(headers[i], 4, i, 2, 0);
        }
        writer.writeAnywhere("eAndon", 4, 7, 0, 3);
        writer.writeAnywhere("Type", 5, 7);
        writer.writeAnywhere("Detail", 5, 8);
        writer.setCurrentStyle(title, center, fontRed);
        writer.writeAnywhere("Alert Duration [M]", 5, 9);

        writer.freeze(6, 0);

        InputStream stream = writer.exportToFile();

        FileUtil.writeToDisk("src/main/resources/basic-example.xlsx", stream, true);
    }
}