package write;

import io.github.nambach.excelutil.core.Editor;
import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import io.github.nambach.excelutil.util.FileUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.InputStream;


public class Sample3 {
    static final String PRIMARY_COLOR = "#990000";
    static final String GRAY = "#eaeaea";
    static final String BROWN = "#f0ebdc";
    static final Style BASED = Style.builder().fontName("Tahoma").fontSize((short) 10).bold(true)
                                    .indentation((short) 1).verticalAlignment(VerticalAlignment.CENTER).build();
    static final Style LARGE_FONT = Style.builder().fontName("Tahoma").fontSize((short) 12).bold(true)
                                         .horizontalAlignment(HorizontalAlignment.CENTER)
                                         .verticalAlignment(VerticalAlignment.CENTER).build();
    static final Style HEADER = Style.builder(BASED).fontColorInHex("#ffffff").backgroundColorInHex(PRIMARY_COLOR).build();
    static final Style BORDER = Style.builder(BASED).border(BorderSide.FULL).build();
    static final Style ALIGN_RIGHT = Style
            .builder(BASED).horizontalAlignment(HorizontalAlignment.RIGHT).build();
    static final Style BG_BROWN = Style.builder(BASED).border(BorderSide.FULL).backgroundColorInHex(BROWN).build();


    public static void main(String[] args) {
        Editor editor = new Editor();
        editor.goToSheet(0)
              .configSheet(cf -> cf.setColumnWidth(214, 0, 1, 2, 3)
                                   .setRowHeightInPoints(30, 0)
                                   .hideGrid(true))
              .useStyle(HEADER)
              .writeCell(c -> c.text("Travel Services Invoice").colSpan(2).style(s -> s.fontSize((short) 16)))
              .applyStyle(HEADER, "C1", "D1")
              .useStyle(BASED)
              .goToCell("A3").text("[Travel Company Name]")
              .moveDown().text("[Street Address]")
              .moveDown().text("[Address 2]")
              .moveDown().text("[City, ST  ZIP Code]")
              .moveDown().text("[Phone]")
              .moveDown().text("[Web Site]")
              .moveDown().text("[E-Mail]")
              .moveDown().text("[Fax]")
              .moveDown(2).text("[Invoice Date]")
              .useStyle(ALIGN_RIGHT)
              .goToCell("B3").text("Invoice No.")
              .moveDown().text("Bill To")
              .moveDown().text("Address")
              .moveDown(3).text("E-Mail")
              .moveDown().text("Phone")
              .moveDown(2).text("Deposit Received")
              .moveDown().text("Invoice Total")
              .moveDown().text("Total Amount Due")
              .moveDown().text("Amount Paid")
              .applyStyle(BORDER, "C3", "C9")
              .applyStyle(BORDER, "C11", "C14")
              .goToCell("A16").useStyle(BG_BROWN).text("Destination")
              .moveDown().text("Travel Dates")
              .moveDown().text("No. of Travelers")
              .goToCell("C18").writeCell(c -> c.text("Tax Rate").style(s -> s.horizontalAlignment(HorizontalAlignment.RIGHT)))
              .applyStyle(BORDER, "B16", "B18")
              .applyStyle(BORDER, "D18")
              .goToCell("D4").useStyle(LARGE_FONT).writeCell(c -> c.text("Total Amount Due on:").style(s -> s.fontColorInHex(PRIMARY_COLOR)))
              .moveDown().text("[Due Date]")
              .goToCell("D10").text("Thanks for letting us")
              .moveDown().text("serve you!");

        InputStream stream = editor.exportToFile();
        FileUtil.writeToDisk("C:\\Users\\Nam Bach\\Desktop\\invoice.xlsx", stream, true);
    }
}
