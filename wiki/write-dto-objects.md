# **A Simple Usage**

To write DTO objects, use class `DataTemplate<T>`. A DataTemplate is a template of DTO type `<T>` holding rules to extract object's fields and write as according columns. Column's title will be generated based on field name, therefore DTO class should follow `camelCase` convention properly.

```java
public class Main {

    static DataTemplate<Book> BOOK_TEMPLATE = DataTemplate
            .fromClass(Book.class)
            .includeAllFields();

    public static void main(String[] args) {
        InputStream stream = BOOK_TEMPLATE.writeData(books);

        FileUtil.writeToDisk(".../books.xlsx", stream, true);
    }
}
```

# **Customize Styles**

Since Apache POI has some [limitations](http://poi.apache.org/apidocs/dev/org/apache/poi/ss/SpreadsheetVersion.html#EXCEL97) regard to styling, **ExcelUtil** provides `Style` as an alternative wrapper to the original POI CellStyle. It allows you to configure much as possible as CellStyle, while handles caching automatically underneath.

To help the caching mechanism work efficiently, it is recommended to pre-define all styles before doing the writing process.

```java
Style BASED_STYLE = Style
    .builder()
    .fontName("Calibri")
    .fontSize((short) 12)
    .build();

// it is able to accumulate previous style. It will not affect
// the caching mechanism as long as you pre-define them
Style HEADER_STYLE = Style
    .builder(BASED_STYLE)
    .fontColorInHex("#ffffff")
    .backgroundColorInHex("#191970")
    .border(BorderSide.FULL)
    .horizontalAlignment(HorizontalAlignment.LEFT)
    .build();
```

Below is an example of conditional styling.

```java
public class Main {
    static final Style DATA_STYLE = ...
    static final Style HIGH_RATE = ...
    static final Style FAVORITE_ONE = ...

    static final DataTemplate<Book> BOOK_TEMPLATE = DataTemplate
            .fromClass(Book.class)
            .column(c -> c.field("isbn").title("ISBN"))  // customize column title
            .includeFields("title", "author")
            .column(c -> c.title("Category")
                          .transform(book -> book.getCategory().getName()))  // derive new column
            .column(c -> c.field("rating")
                          .conditionalStyle(book -> book.getRating() > 4 ?  // styles with conditions
                                                    HIGH_RATE : null))
            .config(cf -> cf.startAtCell("A2")
                            .autoSizeColumns(true)
                            .headerStyle(HEADER_STYLE)
                            .dataStyle(DATA_STYLE)
                            .conditionalRowStyle(book -> book.getTitle() // selective styling
                                            .contains("Harry Potter") ? FAVORITE_ONE : null));

    public static void main(String[] args) {
        InputStream stream = BOOK_TEMPLATE.writeData(books);

        FileUtil.writeToDisk(".../books.xlsx", stream, true);
    }
}
```

#### Result

![Result](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/custom-styles.png)

# **Merge Rows**

You can merge your data rows, either based on same cell values or a particular value that you specify.

Before doing so, you might want to sort your data so that the merging process can perform correctly.

```java
books.sort(Comparator
     .comparing((Book book) -> book.getCategory().getId())
     .thenComparing(comparing(Book::getSubCategory).reversed())
     .thenComparing(Book::getTitle));
```

Here is example of how to configure merge rows.

```java
Style VCENTER = Style.builder().verticalAlignment(VerticalAlignment.CENTER).build();

DataTemplate<Book> BOOK_TEMPLATE = DataTemplate
    .fromClass(Book.class)
    .includeFields("title")
    .column(c -> c.field("subCategory")
                    .style(VCENTER)
                    .mergeOnValue(true))  // merge cells with the same value consecutively
    .column(c -> c.title("Category")
                    .style(VCENTER)
                    .transform(book -> book.getCategory().getName())
                    .mergeOnId(book -> book.getCategory().getId()))  // merge on derived value
    .config(cf -> cf.startAtCell("A2")
                    .autoSizeColumns(true));
```

#### Result

![Result](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/merge-rows.png)

# Expand Rows

The reverse flow of merging rows can also be done - instead of merging rows on same value, if object has a field as collection, you can expand that collection on multiple rows, other fields will remain in only one row.

```java
DataTemplate<Category> CATEGORY_TEMPLATE = DataTemplate
    .fromClass(Category.class)
    .includeFields("name")
    .column(c -> c.field("books")
                  .asList(Book.class, bookCols -> bookCols
                        .includeFields("isbn", "title")
                        .column(bc -> bc.field("words").asList())));
```

#### Result

![Result](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/expand-rows.png)