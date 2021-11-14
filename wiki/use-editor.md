The fundamental idea of this library is the `Editor` class. It acts as a wrapper of the Apache POI `Workbook` to provide high-level functions that easily manipulate the Excel file. That means you can either read or write Excel using `Editor`.

`Editor` provides 3 types of methods:

1. **Navigation methods**: which allow you to iterate through sheets, or go to any specific cell with address.

2. **Writing methods**: which allow you to apply styles and constraints, write data as table or as cells template.

3. **Reading methods**: which allow you to read a specific cell's value, or read a whole table as list.

# Open a new `Editor`

To create a new empty Excel file, simply create a new empty `Editor`. By default, `Editor` will use `XSSFWorkbook`.

```java
// use try-with-resource to automatically release memory
try (Editor editor = new Editor()) {
    ...
}
```

To open a current Excel file, you can either read it as `InputStream` or as `Workbook`.

```java
InputStream stream = FileUtil.readFromDisk("...");

// read directly from stream
try (Editor editor = new Editor(stream)) {
    ...
}

// open and read as Workbook
Workbook xls = new HSSFWorkbook(stream);
try (Editor editor = new Editor(xls)) {
    ...
}
```

# Navigation in `Editor`

You can navigate to a sheet via its name or its index. If that sheet does not exist, `Editor` will create one for you.

```java
editor.goToSheet(3); // If not found, editor will append new sheet
editor.goToSheet("My Sheet"); // If not found, editor will create a new one with provided name
```

`Editor` implements `Iterable<Sheet>` (POI Sheet), so that you can iterate through existing sheets using the enhanced loop (you can use the traditional loop as well).

```java
// traditional loop
for (int i = 0; i < editor.getTotalSheets(); i++) {
    editor.goToSheet(i);
    ...
}


// enhanced loop
for (Sheet sheet : editor) {
    // No need to call .goToSheet(),
    // because 'sheet' is currently selected in editor 
    ...
}
```

To go to a cell, you can either specify the coordinate in number (count from 0) or the string address.

```java
editor.goToCell(3, 2); // row, col
// or
editor.goToCell("C4");
```

`Editor` also has the same set of navigation methods as `Template` that allows you to move next or down along the spreadsheet, which is appropriate for writing free-style template.

```java
editor.goToCell("C4")...
      .down()...  // go to next down cell
      .down(3)... // skip 2 rows
      .next()...  // go to next right cell
      .enter()... // enter next row and go to first cell
```

# Write data

You can use `Editor` to either write a list of DTO or a template of cells as we introduced in previous pages.

To write a list of DTO, you will need to specify a `DataTemplate<T>` (as we introduced in section [Write DTO list](https://github.com/nambach/ExcelUtil/wiki/Write-DTO-List)).

```java
DataTemplate<Book> template = ...
List<Book> books = ...

editor.writeData(template, books);

ByteArrayInputStream stream = editor.exportToFile();
```

With cells template, `Editor` has similar interfaces as `Template` as we mentioned in section [Create Custom Template](https://github.com/nambach/ExcelUtil/wiki/Custom-Template). To write a pre-defined template, simply do as below.

```java
Template template = ...

editor.writeTemplate(template);
```

# Read data

To read a table section as a list of DTO, you need to provide a `ReaderConfig<T>` (as we introduced in [Read data as DTO list](https://github.com/nambach/ExcelUtil/wiki/Read-list-of-DTO)).

```java
ReaderConfig<Book> config = ...

Result<Book> books = editor
    .gotoCell("A2") // navigate to started cell
    .readSection(config);
```

The best part of `Editor` is that you can read any cell's value in the most flexible way.

```java
String companyName = editor.gotoCell("A3").readString();
LocalDateTime eta = editor.gotoCell("A12").readLocalDateTime();
Integer batchs = editor.gotoCell("C8").readInt(); // if there is no value, null will be returned
```