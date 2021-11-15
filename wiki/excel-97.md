# Open Excel 97-2003 `.xls` file

To open an existing file, you can either read the file as `InputStream` or open it as Apache POI `HSSFWorkbook`, and then pass it as argument of `Editor`.

```java
InputStream stream = FileUtil.readFromDisk(".../file.xls");

// Option 1: open as stream
Editor editor = new Editor(stream));


// Option 2: open as HSSFWorkbook
Workbook wb = new HSSFWorkbook(stream);
Editor editor = new Editor(wb));
```

To open a blank `.xls` file, you can create an empty `HSSFWorkbook` and pass it into an `Editor`.

```java
// remember to surround with try-with-resource
try (Workbook workbook = new HSSFWorkbook();
     Editor editor = new Editor(workbook)) {
    ...
}
```

# Colors in Excel 97-2003

One problem with `.xls` format is that we only have maximum of 57 colors to use. These colors are defined in `IndexedColors` (Apache POI 5.0.0). Of course we can override built-in colors with custom values according to [the official guide](http://poi.apache.org/components/spreadsheet/quick-guide.html#CustomColors), but our code would be very messy if there are many colors to override.

**ExcelUtil** provide 2 strategies to process colors in `.xls` format:
1. Override built-in colors with exact hex-values provided from `Style`. With this strategy, you need to assure the total colors must not exceed the maximum number (which is 57).
2. Replace user's colors with the most similar built-in values.

By default, **ExcelUtil** uses strategy 2. If you want to explicitly specify the strategy, config the `editor` as below.

```java
// Strategy 1: override built-in values
editor.configWorkbook(f -> f.setXLSColorPolicy(HSSFColorCache.Policy.OVERRIDE));

// Strategy 2: use most similar. This is the default configuration.
editor.configWorkbook(f -> f.setXLSColorPolicy(HSSFColorCache.Policy.USE_MOST_SIMILAR));
```

Here is the comparison between 2 strategies.

![Comparison](https://github.com/nambach/ExcelUtil/blob/master/wiki/img/excel97compare.png)