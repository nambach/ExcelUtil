One of the problem when writing data is that Excel file has some [limitations](http://poi.apache.org/apidocs/dev/org/apache/poi/ss/SpreadsheetVersion.html#EXCEL97) regarding to styles:
- `.xls` file has maximum of 4,000 cell styles
- `.xlsx` file has maximum of 64,000 cell styles.

Therefore, in case we only use Apache POI, if our code is not carefully written, the chance is that we will have some error like this
```
Exception: java.lang.IllegalStateException: The maximum number of Cell Styles was exceeded. You can define up to 64000 style in a .xlsx Workbook
```

# Cache styles using **ExcelUtil**

**ExcelUtil** implements a caching mechanism based on tree structure to re-use styles from Apache POI.

`Style` are declared before being used in exporting data.

```java
Style BASED_STYLE = Style
    .builder()
    .fontName("Calibri")
    .fontSize((short) 12)
    .build();

// it is able to accumulate previous style.
Style HEADER_STYLE = Style
    .builder(BASED_STYLE)
    .fontColorInHex("#ffffff")
    .backgroundColorInHex("#191970")
    .border(BorderSide.FULL)
    .horizontalAlignment(HorizontalAlignment.LEFT)
    .build();
```

## 1. Style combination

Styles can be applied in 4 places as below:
1. Column style 
2. (Conditional) Cell style
3. Background style
4. (Conditional) Row style

```java
DataTemplate<Book> template = DataTemplate
    .fromClass(Book.class)
    ...
    .column(c -> c.field("subCategory").style(VCENTER)) // 1. Column style
    .column(c -> c.field("rating")
                  .conditionalStyle(book -> book.getRating() > 4 ?  // 2. Conditional cell style
                                            HIGH_RATE : null))
    .config(cf -> cf.dataStyle(DATA_STYLE) // 3. Background style
                    .conditionalRowStyle(book -> book.getTitle() // 4. Conditional row style
                                    .contains("Harry Potter") ? FAVORITE_ONE : null));
```

If a cell has styles in all 4 types simultaneously, those styles will be combined altogether with priorities as below:

![Priorities](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/style-priority.png)

## 2. Cache style using tree

Each `Style` object will have a unique UUID. When writing data, `Style` will be generated into POI `CellStyle` and applied to `Workbook`. The final goal of style caching is to re-use `CellStyle` based on its original UUID.

To do so, **ExcelUtil** uses tree structure. Each node will store `CellStyle` as value and its UUID as identity. When a `Style` is applied, the according POI `CellStyle` will be generated and stored in a node. Next time, if that style is applied again, the program will look up in tree and re-use the existing `CellStyle`.

For combined styles, to prevent excessive style creation, the combined `CellStyle` will only be created once and then stored in the path made of IDs of ingredient styles. Next time, if there is a need to combine styles again, the program will look up path of ingredient styles in order and re-use the existing `CellStyle`.

![Tree](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/style-tree.png)


# Working with `Style`

Now you know the underlying mechanism of style caching. To effectively support this method, just keep in mind only one thing:

> Never create new styles while declaring `DataTemplate<T>`, `Template`, or writing data using `Editor`. Creating `Style` dynamically will lead to overcontrol the number of generated `CellStyle` and break the limitation. The best way is to define all styles statically before invoking them.