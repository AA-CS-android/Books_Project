package com.hw.books_project.models;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BookTest {

    @Test
    public void testBookCreation() {
        List<String> genres = Arrays.asList("Fiction", "Fantasy");
        Book book = new Book("123", "The Lord of the Rings", "http://example.com/cover.jpg", genres, "J.R.R. Tolkien", "Allen & Unwin", "978-0618640157");

        assertEquals("123", book.getBookId());
        assertEquals("The Lord of the Rings", book.getName());
        assertEquals("http://example.com/cover.jpg", book.getCoverImageUrl());
        assertEquals(genres, book.getGenres());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
        assertEquals("978-0618640157", book.getBarcode());
    }
}