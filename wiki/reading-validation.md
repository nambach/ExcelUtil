**ExcelUtil** provides built-in validation APIs that are quite similar to [Jakarta Bean Validation](https://beanvalidation.org/).

```java
ReaderConfig<Book> readerConfig = ReaderConfig
    .fromClass(Book.class)
    .titleAtRow(1)
    .dataFromRow(2)
    .column(0, "isbn", v -> v.isString().minLength(10, "ISBN must be at least 20 chars"))
    .column(1, "title", v -> v.notNull("Title must not be null"))
    .column(2, "author", v -> v.isString().notBlank("Must provide author"))
    .column(3, "rating", v -> v.isDecimal().notNull());

// Read data
Result<Book> books = readerConfig.readSheet(stream);

// Check error here

for (RowError row : books.getErrors()) {
    int atRow = row.getExcelIndex(); // row occurs error
    String message = row.getInlineMessage(); // summarized error message
    System.out.println("Row " + atRow + ": " + message);

    // object's detail errors
    ObjectError objectError = row.getObjectError();
    for (FieldError field : objectError) {
        String dtoField = field.getFieldName(); // field name
        List<String> dtoFieldErrors = field.getMessages(); // field's errors
    }
}
```

For example, with the below input, we will get these messages

![Result](https://raw.githubusercontent.com/nambach/ExcelUtil/master/wiki/img/reader-validation-1.png)

```
Row 4: 'isbn': Length must between 10 and 13; 'rating': must not be null
Row 5: 'isbn': Length must between 10 and 13; 'author': Must provide author; 'rating': Rating must be between 0 and 5
```

If you want to isolate validation logic into separated objects for further reuse, you can do as below.

```java
// Declare independent validation object
Validator<Book> bookValidator = Validator
    .fromClass(Book.class)
    .on(f -> f.field("isbn")
              .validate(v -> v.isString().notNull()
                              .lengthBetween(10, 13, "Length must between 10 and 13")))
    .on(f -> f.field("title")
              .validate(v -> v.isString().notBlank("Title must be provided")))
    .on(f -> f.field("author")
              .validate(v -> v.isString().notBlank("Author must be provided")))
    .on(f -> f.field("rating")
              .validate(v -> v.isDecimal().notNull()
                              .between(0, 5, "Rating must be between 0 and 5")));

// Then add it to the reader
ReaderConfig<Book> readerConfig = ReaderConfig
    .fromClass(Book.class)
    .validator(bookValidator)
    ...
```

## Manual validation

For more flexible validations, you can specify it inside `.handler()` and `.beforeAddingItem()` as below

```java
ReaderConfig<Book> readerConfig = ReaderConfig
    .fromClass(Book.class)
    ...
    .handler(set -> set.atColumn(3)
                       .handle((book, cell) -> {
                           // check if something wrong
                           Double rating = cell.readDouble();
                           if (rating == null) {
                               // set the error, and the row index with column title will be auto added
                               cell.setError("rating must not be null");
                               // or throw error and exit the reading process immediately
                               cell.throwError("rating must not be null");
                           }
                       }))
    .beforeAddingItem((book, row) -> {
        // manual validation...

        // you can get current row's errors after reading the current item
        List<RowError> errors = row.getErrors();

        // skip item if it is not valid to add
        row.skipThisObject();

        // or stop immediately to save workload
        row.throwError("Stop due to...");
        // or simply this
        row.terminateNow();
    });
```

