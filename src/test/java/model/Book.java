package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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
    public static class Category {
        private long id;
        private String name;

        public Category(long id, String name) {
            this.id = id;
            this.name = name;
        }

        private List<Book> books;
    }
}
