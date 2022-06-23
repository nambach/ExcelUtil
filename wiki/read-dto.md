
The building block to read data is `ReaderConfig<T>`. It holds rules to map each column into according field of class `<T>`, which is the opposite of the writing process.

Rule can be configured either based on column index, or based on column title. If you want to base on title, index of title row must be provided via `.titleAtRow(int)`

```java
ReaderConfig<Book> BOOK_READER = ReaderConfig
        .fromClass(Book.class)
        .titleAtRow(0) // provide title row index
        .dataFromRow(1)
        .column(0, "ibsn") // configure by column index
        .column(1, "title")
        .column("Date", "firstPublished") // by column title
        [...]
```

You can directly retrieve the config from your already defined `DataTemplate<T>`.

```java
DataTemplate<T> bookTemplate = ...

ReaderConfig<Book> readerConfig = bookTemplate.getReaderConfig();
```

The config provides 2 options: reading a single sheet as list/stream, or reading multiple/all sheets as map of list.

```java
InputStream stream = FileUtil.readFromDisk(".../book.xlsx");

/* Read a single sheet */
Result<Book> books = readerConfig.readSheet(stream);
for (Book book : books) {
    // Result<Book> is iterable
}
// convert to stream directly
Stream<Book> bookStream = readerConfig.readSheetToStream(stream);


/* Read multiple sheets */
Map<String, Result<Book>> bookMap = readerConfig.readAllSheets(stream);
bookMap.forEach((sheetName, bookList) -> {
    for (Book book : bookList) {
        // Result<Book> is iterable
    }
});
```



For more flexible process while reading data, use built-in callback handler as below.



```java
ReaderConfig<Book> READER_CONFIG = ReaderConfig
    .fromClass(Book.class)
    .titleAtRow(0)
    .dataFromRow(1)
    .column(0, "ibsn")
    .column(1, "title")
    .handler(set -> set.atColumn(2)
                       .handle((book, cell) -> {
                           String value = cell.readString();
                           book.getCategory().setName(value);
                       }))
    .handler(set -> set.fromColumn(3)
                       .handle((book, cell) -> {
                           String title = cell.getColumnTitle();
                           if (title.contains("Rating in")) {
                               String year = title.substring(10);
                               Double rating = cell.readDouble();
                               book.getRatingMap().put(year, rating);
                           }
                       }));
```
