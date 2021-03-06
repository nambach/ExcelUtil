package write;

import io.github.nambach.excelutil.constraint.Constraint;
import io.github.nambach.excelutil.core.Editor;
import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;


public class Sample3 {
    static final String PRIMARY_COLOR = "#990000";
    static final String GRAY = "#808080";
    static final String BROWN = "#f0ebdc";
    static final Style BASED = Style.builder().fontName("Tahoma").fontSize((short) 10).bold(true)
                                    .indentation((short) 1).verticalAlignment(VerticalAlignment.CENTER).build();
    static final Style LARGE_FONT = Style.builder().fontName("Tahoma").fontSize((short) 12).bold(true)
                                         .horizontalAlignment(HorizontalAlignment.CENTER)
                                         .verticalAlignment(VerticalAlignment.CENTER).build();
    static final Style HEADER = Style.builder(BASED).fontColorInHex("#ffffff").backgroundColorInHex(PRIMARY_COLOR).build();
    static final Style BORDER_BOLD = Style.builder(BASED)
                                          .border(b -> b.side(BorderSide.FULL).hexColor(GRAY))
                                          .build();
    static final Style BORDER = Style.builder(BORDER_BOLD).bold(false).build();
    static final Style ALIGN_RIGHT = Style.builder(BASED)
                                          .horizontalAlignment(HorizontalAlignment.RIGHT)
                                          .build();
    static final Style BG_BROWN = Style.builder(BASED)
                                       .border(b -> b.side(BorderSide.FULL).hexColor(GRAY))
                                       .backgroundColorInHex(BROWN).build();

    static final Constraint countries = Constraint.builder()
                                                  .dropdown("VN", "US")
                                                  .build();

    @SneakyThrows
    public static void main(String[] args) {
        @Cleanup HSSFWorkbook workbook = new HSSFWorkbook();
        @Cleanup Editor editor = new Editor(workbook);

        int[] colIndexes = new int[]{0, 1, 2, 3};
        editor.goToSheet("My Sheet")
              .configSheet(cf -> cf.setColumnWidth(214, colIndexes)
                                   .setRowHeightInPoints(30, 0)
                                   .hideGrid(true)
                                   .setZoom(70));

        // Header part
        editor.useStyle(HEADER)
              .writeCell(c -> c.text("Travel Services Invoice")
                               .colSpan(2)
                               .style(s -> s.fontSize((short) 16)))
              .applyStyle(HEADER, "C1:D1");

        // Form part
        editor.goToCell("B3").useStyle(ALIGN_RIGHT).text("Invoice No.")
              .down().text("Bill To")
              .down().text("Address")
              .down(3).text("E-Mail")
              .down().text("Phone")
              .down(2).text("Deposit Received")
              .down().text("Invoice Total")
              .down().text("Total Amount Due")
              .down().text("Amount Paid")
              .applyStyle(BORDER, "C3:C9", "C11:C14")
              .applyConstraint(countries, "C5");

        // To be filled in
        editor.goToCell("A3").useStyle(BASED).text("[Travel Company Name]")
              .down().text("[Street Address]")
              .down().text("[Address 2]")
              .down().text("[City, ST  ZIP Code]")
              .down().text("[Phone]")
              .down().text("[Web Site]")
              .down().text("[E-Mail]")
              .down().text("[Fax]")
              .down(2).text("[Invoice Date]");

        // Traveler part
        editor.goToCell("A16").useStyle(BG_BROWN).text("Destination")
              .down().text("Travel Dates")
              .down().text("No. of Travelers")
              .goToCell("C18").writeCell(c -> c.text("Tax Rate").style(s -> s.horizontalAlignment(HorizontalAlignment.RIGHT)))
              .applyStyle(BORDER, "B16:B18", "D18");

        // Large title on right
        editor.goToCell("D4").useStyle(LARGE_FONT)
              .writeCell(c -> c.text("Total Amount Due on:").style(s -> s.fontColorInHex(PRIMARY_COLOR)))
              .down().text("[Due Date]")
              .goToCell("D10").text("Thanks for letting us")
              .down().text("serve you!");

        // Detail table
        editor.goToCell("A20").useStyle(BG_BROWN).text("Service")
              .next().text("Description")
              .next().text("Amount per Traveler")
              .next().text("Total Amount")
              .enter().useStyle(BORDER_BOLD).text("Air Transportation")
              .enter().text("Ground Transportation")
              .enter().text("Lodging")
              .enter().text("Tours")
              .enter().text("Other")
              .applyStyle(BORDER, "A21:D30");

        @Cleanup InputStream stream = editor.exportToFile();
        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\invoice2.xls", stream, true);
    }
}
