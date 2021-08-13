**ExcelUtil** provides `Editor` as the main tool to write Excel. It allows you to write cells individually by navigating throughout the sheet.

```java
    int[] colIndexes = new int[]{0, 1, 2, 3};

    Editor editor = new Editor();

    // Newly created editor has no sheet, therefore need to create before writing
    editor.goToSheet(0) // create-if-not-exist automatically
          .configSheet(cf -> cf.setColumnWidth(214, colIndexes)
                               .setRowHeightInPoints(30, 0)
                               .hideGrid(true));

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
          .applyStyle(BORDER, "C3:C9", "C11:C14", "C17");

    [...]
```

(You can see the full sample [here](https://github.com/nambach/ExcelUtil/blob/master/src/test/java/write/Sample3.java))

### Result

![Result](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/custom-template.png)

# Reused Template

Besides `DataTemplate<T>`, you can use `Template` to create a reusable set of cells. It provides the same interfaces as `Editor` except for those related to sheet configuration. You can simply replace editor by template as below:

```java
    Template template = new Template();

    // replace all 'editor' occurrences with 'template'

    // Header part
    template.useStyle(HEADER)
            .writeCell(c -> c.text("Travel Services Invoice"))
            ...

    template.goToCell("B3")
            .useStyle(ALIGN_RIGHT).text("Invoice No.")
            ...

    [...]

    InputStream stream = template.getFile();
    FileUtil.writeToDisk(".../invoice.xlsx", stream, true);
```
