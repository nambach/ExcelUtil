One of the problem when writing data is that Excel file has some [limitations](http://poi.apache.org/apidocs/dev/org/apache/poi/ss/SpreadsheetVersion.html#EXCEL97) regarding to styles:
- `.xls` file has maximum of 4,000 cell styles
- `.xlsx` file has maximum of 64,000 cell styles.

Therefore, in case we only use Apache POI, if our code is not carefully written, we may face some error likes this.
```
Exception: java.lang.IllegalStateException: The maximum number of Cell Styles was exceeded.
You can define up to 64000 style in a .xlsx Workbook
```

# The mechanism of style caching in **ExcelUtil**

**ExcelUtil** implements a caching mechanism based on tree structure to re-use styles from Apache POI.

`Style` objects are declared before being used in exporting data.

```java
Style BASED_STYLE = Style
    .builder()
    .fontName("Calibri")
    .fontSize((short) 12)
    .build();

Style HEADER_STYLE = Style
    .builder(BASED_STYLE) // accumulate existing style
    .fontColorInHex("#ffffff")
    .backgroundColorInHex("#191970")
    .border(BorderSide.FULL)
    .horizontalAlignment(HorizontalAlignment.LEFT)
    .build();
```

## 1. Style combination

Styles can be applied in 4 places as below:
- Column style 
- (Conditional) Cell style
- Background style
- (Conditional) Row style

```java
DataTemplate<Book> template = DataTemplate
    .fromClass(Book.class)
    ...
    .column(c -> c.field("rating")
                  .style(BROWN) // Column style
                  .conditionalStyle(book -> book.getTitle().contains("Sorcerer's")
                                    ?  HIGH_RATE : null)) // Cell style
    .config(cf -> cf.dataStyle(DATA_STYLE) // Background style
                    .conditionalRowStyle(book -> book.getTitle() // Row style
                                    .contains("Harry Potter") ? FAVORITE_ONE : null));
```

There are some situations, a cell can have multiple styles overlapping each other. In that case, styles will be combined altogether with priorities as below:

![Priorities](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/style-priority.png)

Here is an example of output file may look like.

![Example](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/style-priority-example.png)

## 2. Caching styles using tree

Each `Style` object will have a unique ID. When writing data, `Style` will be generated into POI `CellStyle`. The goal of style caching is to re-use `CellStyle` based on its original ID.

To do so, **ExcelUtil** uses tree structure. Each node will store the `CellStyle` as value and its original ID as identity. When applying a `Style`, the corresponding `CellStyle` will be generated and stored in a node. Next time, if that style is applied again, the program will look up in tree and re-use the existing `CellStyle`. These styles are called **Atomic CellStyle**

For combined styles, to prevent excessive creation, the combined `CellStyle` will only be created once and then stored in the path made of IDs of ingredient styles. Next time, if there is a need to combine styles again, the program will look up the path of ingredient styles in order and re-use the existing `CellStyle`. These are called **Combined CellStyle**.

![Tree](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/style-tree.png)

Note that, for styles that are declared by *accumulating existing ones*, they are treated as **Atomic CellStyle**.

```java
// Atomic style
Style BASED_STYLE = Style
    .builder()
    .fontName("Calibri")
    .build();

// Also atomic style
Style HEADER_STYLE = Style
    .builder(BASED_STYLE) // accumulate existing style
    .fontColorInHex("#ffffff")
    .backgroundColorInHex("#191970")
    .build();
```

# Working with `Style`

Now you know the underlying mechanism of style caching. To effectively support this method, just keep in mind only one thing:

> Never create new styles while declaring `DataTemplate<T>`, `Template`, or writing data using `Editor`. Creating `Style` dynamically will lead to overcontrol the number of generated `CellStyle` and break the limitation. It is best to define all styles statically.