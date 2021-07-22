package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class Book {
    private String isbn;
    private String title;
    private double rating;
    private Date firstPublished;
    private String author;
    private String subCategory;
    private Category category;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Category {
        private long id;
        private String name;
    }
}
