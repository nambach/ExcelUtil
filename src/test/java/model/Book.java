package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class Book {
    private String isbn;
    private String title;
    private double rating;
    private Date firstPublished;
    private String author;
    private String subCategory;
    private Category category;
    private List<String> chars;

    public Book() {
    }

    public Book(String isbn, String title, double rating, Date firstPublished, String author, String subCategory, Category category) {
        this.isbn = isbn;
        this.title = title;
        this.rating = rating;
        this.firstPublished = firstPublished;
        this.author = author;
        this.subCategory = subCategory;
        this.category = category;
        this.chars = Arrays.asList(title.split(" "));
    }

    @Getter
    @Setter
    public static class Category {
        private long id;
        private String name;
        private List<Book> books;

        public Category(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
