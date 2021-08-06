package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Constant {
    public static final Map<Integer, Book.Category> CAT = new HashMap<Integer, Book.Category>() {{
        put(0, new Book.Category(0, "Fiction"));
        put(1, new Book.Category(1, "Non-fiction"));
        put(2, new Book.Category(2, "Science"));
    }};
    private static final SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
    public static List<Book> BOOKS;

    static {
        try {
            BOOKS = Arrays.asList(
                    new Book("0062315005", "The Alchemist", 3.89,
                             sdf.parse("1/1/1988"), "Paulo Coelho", "Classics", CAT.get(0)),
                    new Book("1593279507", "Eloquent Javascript", 4.14,
                             sdf.parse("15/11/2010"), "Marijn Haverbeke", "Programming", CAT.get(2)),
                    new Book("0671212095", "How to Read a Book", 3.97,
                             sdf.parse("1/1/1940"), "Mortimer Jerome Adler", "Education", CAT.get(1)),
                    new Book("0439136369", "Harry Potter and the Prisoner of Azkaban", 4.57,
                             sdf.parse("8/7/1999"), "J.K. Rowling", "Fantasy", CAT.get(0)),
                    new Book("0062322605", "The State of Affairs", 4.36,
                             sdf.parse("10/10/2017"), "Esther Perel", "Psychology", CAT.get(1)),
                    new Book("9864734563", "君の名は。 Another Side:Earthbound", 3.96,
                             sdf.parse("30/7/2016"), "Arata Kanoh", "Light Novel", CAT.get(0)),
                    new Book("0134685997", "Effective Java", 4.51,
                             sdf.parse("5/6/2001"), "Joshua Bloch", "Programming", CAT.get(2)),
                    new Book("0590353403", "Harry Potter and the Sorcerer's Stone", 4.47,
                             sdf.parse("26/6/1997"), "J.K. Rowling", "Fantasy", CAT.get(0)),
                    new Book("1591162769", "Battle Angel Alita #5: Angel of Redemption", 4.30,
                             sdf.parse("1/5/1993"), "Yukito Kishiro", "Manga", CAT.get(0)),
                    new Book("0446575062", "The Defining Decade", 4.11,
                             sdf.parse("17/4/2012"), "Meg Jay", "Education", CAT.get(1)),
                    new Book("0062316095", "Sapiens: A Brief History of Humankind", 4.40,
                             sdf.parse("17/4/2012"), "Yuval Noah Harari", "History", CAT.get(2))
            );

            for (Book.Category category : CAT.values()) {
                category.setBooks(BOOKS.stream().filter(b -> b.getCategory() == category).collect(Collectors.toList()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
