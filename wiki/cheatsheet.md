| Class | Functionality | Usage |
| --- | --- | --- |
| `Editor` | This is the core component of **ExcelUtil**, act as a wrapper of Apache POI `Workbook`, allow to both read and write data into Excel file. | See [Use `Editor`](https://github.com/nambach/ExcelUtil/wiki/Best-Practices:-Use-Editor) | 
| `DataTemplate<T>` | A template to export DTO list as Excel table | See [Write DTO list](https://github.com/nambach/ExcelUtil/wiki/Write-DTO-List) |
| `Template` | A template to write pre-defined cells | See [Custom Template](https://github.com/nambach/ExcelUtil/wiki/Custom-Template) |
| `Style` | An object to describe cell style | See [Write DTO list](https://github.com/nambach/ExcelUtil/wiki/Write-DTO-List) |
| `ReaderConfig<T>` | A configuration object to read Excel table as list of DTOs | See [Read list of DTO](https://github.com/nambach/ExcelUtil/wiki/Read-list-of-DTO) | 
| `Validator<T>` | A set of rules to validate Java DTO | See [Reading Validation](https://github.com/nambach/ExcelUtil/wiki/Reading-Validation) |