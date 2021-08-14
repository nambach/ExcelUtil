package style;

import io.github.nambach.excelutil.style.BorderSide;
import io.github.nambach.excelutil.style.Style;
import org.junit.Assert;
import org.junit.Test;

public class TestStyle {

    @Test
    public void testClone() {
        Style style = Style.builder()
                           .fontName("Times New Roman")
                           .fontSize((short) 13)
                           .border(BorderSide.FULL)
                           .build();

        Style clone = Style.builder(style).build();

        Assert.assertNotSame(style.getUuid(), clone.getUuid());

        Assert.assertEquals(style.getFontName(), clone.getFontName());

        Assert.assertEquals(style.getFontSize(), clone.getFontSize());

        Assert.assertEquals(style.getBorders(), clone.getBorders());

        System.out.println(style);
        System.out.println();
        System.out.println(clone);
    }
}
