package navigation;

import io.github.nambach.excelutil.core.Editor;
import org.apache.poi.ss.usermodel.Sheet;

public class NavigationSheet {
    public static void main(String[] args) {
        Editor editor = new Editor();
        for (Sheet sheet : editor) {
            editor.getSheetName();
        }

        for (int i = 0; i < editor.getTotalSheets(); i++) {
            editor.goToSheet(i);
            Sheet sheet = editor.getCurrentPoiSheet();
        }

        editor.goToSheet(0);
        editor.goToSheet("Sheet 1");

        editor.goToCell(0, 0);
        editor.goToCell("C4");

    }
}
