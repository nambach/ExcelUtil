# Access the native POI objects

Since **ExcelUtil** is just merely a wrapper of `Workbook`, you can access the original POI object at any time you want.

```java
Editor editor = new Editor();

...

Workbook wb = editor.getPoiWorkbook();
Sheet sheet = editor.getCurrentPoiSheet();
```

In `ReaderConfig<T>`, you can retrieve the POI `Cell` as below.

```java
ReaderConfig<Book> READER_CONFIG = ReaderConfig
    .fromClass(Book.class)
    ...
    .handler(set -> set.atColumn(2)
                       .handle((book, cell) -> {

                           Cell poiCell = cell.getPoiCell();
                           ...
                       }))
```

# Write large data with POI `SXSSFWorkbook`

**ExcelUtil** act as a wrapper of `Workbook`. Therefore you can provide a `SXSSFWorkbook` for processing large data (read more at [official page](http://poi.apache.org/components/spreadsheet/how-to.html#sxssf)).

```java
SXSSFWorkbook wb = new SXSSFWorkbook(100); // specify the "window size" of 100 rows
Editor editor = new Editor(wb);
```

In order to let `SXSSFWorkbook` works effectively, please remember **not to set auto-size columns** for the sheet, since the calculation process is expensive.

Here is the comparison when writing 10,000 rows into Excel file. 
- The first benchmark (`LargeSample.testLargeXlsx`) uses `Editor` with the `SXSSFWorkbook` having "window size" of 100. Average runtime is 0.32s.
- The second benchmark uses normal `Editor` with the default `XSSFWorkbook`. Average runtime is 1.26s.

![benchmark_writing](https://raw.githubusercontent.com/nambach/ExcelUtil/master/wiki/img/benchmark_writing.png)

*(Benchmarks are performed using [**Java Microbenchmark Harness**](https://github.com/openjdk/jmh))*

# Drop-down values for cells

To define drop-down values for a cell, use `Constraint` as below.

```java
Constraint countries = Constraint.builder()
    .dropdown("Vietnam", "Japan")
    .build();

...

editor.applyConstraint(countries, "C4");
```

Here is the result.

![Result](https://raw.githubusercontent.com/nambach/ExcelUtil/master/wiki/img/dropdown.png)